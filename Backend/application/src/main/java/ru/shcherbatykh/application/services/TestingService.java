package ru.shcherbatykh.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.broker.KafkaMessageProducer;
import ru.shcherbatykh.application.classes.*;
import ru.shcherbatykh.application.dto.ResponseAboutTestingAllowed;
import ru.shcherbatykh.application.dto.SendingOnTestingResponse;
import ru.shcherbatykh.application.models.*;
import ru.shcherbatykh.application.repositories.CheckTestRepo;
import ru.shcherbatykh.application.repositories.TestDefinitionRepo;
import ru.shcherbatykh.common.model.*;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TestingService {

    private final CheckTestRepo checkTestRepo;
    private final TestDefinitionRepo testDefinitionRepo;
    private final TaskService taskService;
    private final StudentTaskService studentTasksService;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final StatusService statusService;

    public TestingService(CheckTestRepo checkTestRepo, TestDefinitionRepo testDefinitionRepo, TaskService taskService,
                          StudentTaskService studentTasksService,
                          KafkaMessageProducer kafkaMessageProducer, StatusService statusService) {
        this.checkTestRepo = checkTestRepo;
        this.testDefinitionRepo = testDefinitionRepo;
        this.taskService = taskService;
        this.studentTasksService = studentTasksService;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.statusService = statusService;
    }

    public ResponseAboutTestingAllowed getResponseAboutTestingAllowed(int serialNumberOfChapter, int serialNumberOfBlock,
                                                                      int serialNumberOfTask, User user) {
        if (user.getRole() != Role.USER){
            return new ResponseAboutTestingAllowed(true);
        }
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        Task prevTask = taskService.getPreviousTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        if (prevTask != null){
            if (!Objects.equals(studentTasksService.getStatusByUserAndTask(user, prevTask).getName(), "Решена")){
                return new ResponseAboutTestingAllowed(false, ReasonOfProhibitionTesting.PREVIOUS_TASK_NOT_SOLVED);
            }
        }
        if (Objects.equals(studentTasksService.getStatusByUserAndTask(user, task).getName(), "На проверке")){
            return new ResponseAboutTestingAllowed(false, ReasonOfProhibitionTesting.TASK_ON_TEACHER_REVIEW);
        }
        if (Objects.equals(studentTasksService.getStatusByUserAndTask(user, task).getName(), "На рассмотрении")){
            return new ResponseAboutTestingAllowed(false, ReasonOfProhibitionTesting.TASK_ON_TEACHER_CONSIDERATION);
        }
        if (Objects.equals(studentTasksService.getStatusByUserAndTask(user, task).getName(), "На тестировании")){
            return new ResponseAboutTestingAllowed(false, ReasonOfProhibitionTesting.TASK_ON_TESTING);
        }

        return new ResponseAboutTestingAllowed(true);
    }

    public void  sendOnTesting(StudentTask stTask, User teacher, List<String> codes) {
        List<CodeSource> codeSources = codes.stream()
                .map(code -> code.getBytes(StandardCharsets.UTF_8))
                .map(Base64::encodeBase64)
                .map(String::new)
                .map(CodeSource::new)
                .toList();
        CheckTest newCheckTest = new CheckTest(stTask, teacher);
        CheckTest savedCheckTest = checkTestRepo.save(newCheckTest);
        CodeCheckRequest codeCheckRequest = new CodeCheckRequest(
                stTask.getUser().getId().toString(),
                stTask.getTask().getId().toString(),
                savedCheckTest.getId().toString(),
                codeSources
        );
        kafkaMessageProducer.sendCodeCheckMessage(codeCheckRequest);
    }

    public SendingOnTestingResponse testingForStudent(int serialNumberOfChapter, int serialNumberOfBlock,
                                                      int serialNumberOfTask, User user, List<String> codes){
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);

        if (!taskService.saveCodeToFiles(stTask, codes, true)){
            return new SendingOnTestingResponse(false, stTask.getCurrStatus(), AppError.APP_ERR_001);
        } else {
            studentTasksService.setStatusOnTesting(stTask);
            sendOnTesting(stTask, null, codes);
            return new SendingOnTestingResponse(true, statusService.getStatusByName("На тестировании"));
        }
    }

    public SendingOnTestingResponse testingForTeacher(long studentTaskId, User teacher, List<String> codes) {
        if (!taskService.saveCodeToFiles(studentTasksService.findById(studentTaskId), codes, false)){
            return new SendingOnTestingResponse(false, AppError.APP_ERR_001);
        } else {
            sendOnTesting(studentTasksService.findById(studentTaskId), teacher, codes);
            return new SendingOnTestingResponse(true);
        }
    }

    @Transactional
    public CodeCheckResponseResult getTestingResultForStudent(int serialNumberOfChapter, int serialNumberOfBlock,
                                                            int serialNumberOfTask, User student) {
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(student, task);
        CheckTest checkTest = checkTestRepo.findLastByStudentTaskAndTeacherIsNull(stTask);
        if (checkTest == null || checkTest.getCodeCheckResponseResultJson() == null) return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CodeCheckResponseResult codeCheckResponseResult =
                    objectMapper.readValue(checkTest.getCodeCheckResponseResultJson(), CodeCheckResponseResult.class);
            if (!checkTest.isHasBeenAnalyzed()){
                if (Objects.equals(codeCheckResponseResult.getCode(), "CH-000")){
                    if (task.isManualCheckRequired()){
                        studentTasksService.setStatusPassedTests(stTask);
                    } else {
                        studentTasksService.setStatusSolved(stTask);
                    }
                } else {
                    studentTasksService.setStatusNotPassedTests(stTask);
                }
                checkTest.setHasBeenAnalyzed(true);
                checkTestRepo.save(checkTest);
            }
            return codeCheckResponseResult;
        } catch (JsonProcessingException e) {
            log.error("Exception during reading CodeCheckResponse from JSON type to object", e.getMessage());
            return null;
        }
    }

    @Transactional
    public CodeCheckResponseResult getTestingResultForTeacher(long studentTaskId, User teacher) {
        StudentTask stTask = studentTasksService.findById(studentTaskId);
        if (stTask == null) return null;
        CheckTest checkTest = checkTestRepo.findLastByStudentTaskAndTeacher(stTask, teacher);
        if (checkTest == null || checkTest.getCodeCheckResponseResultJson() == null) return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CodeCheckResponseResult codeCheckResponseResult =
                    objectMapper.readValue(checkTest.getCodeCheckResponseResultJson(), CodeCheckResponseResult.class);
            if (!checkTest.isHasBeenAnalyzed()){
                checkTest.setHasBeenAnalyzed(true);
                checkTestRepo.save(checkTest);
            }
            return codeCheckResponseResult;
        } catch (JsonProcessingException e) {
            log.error("Exception during reading CodeCheckResponse from JSON type to object", e.getMessage());
            return null;
        }
    }


    public void saveCodeCheckResponse(CodeCheckResponse codeCheckResponse){
        CheckTest checkTest = checkTestRepo.findById(Long.valueOf(codeCheckResponse.getRequestUuid())).get();
        CodeCheckResponseResult codeCheckResponseResult = new CodeCheckResponseResult(
                codeCheckResponse.getCode(),
                codeCheckResponse.getMessage(),
                codeCheckResponse.getResults()
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String codeCheckResponseResultAsJson = objectMapper.writeValueAsString(codeCheckResponseResult);
            checkTest.setCodeCheckResponseResultJson(codeCheckResponseResultAsJson);
            checkTest.setGettingResultTime(LocalDateTime.now());
            checkTestRepo.save(checkTest);
       } catch (JsonProcessingException e) {
            log.error("Exception during saving CodeCheckResponse to database", e.getMessage());
        }
    }

    public boolean sendCodeTest(long taskId, String codeTest) {
        Task task = taskService.findById(taskId);
        String codeEncoded = new String(java.util.Base64.getEncoder().encode(codeTest.getBytes(StandardCharsets.UTF_8)));
        TestDefinition savedTestDefinition = testDefinitionRepo.save(new TestDefinition(task, codeEncoded));
        TestDefinitionRequest testDefinitionRequest = new TestDefinitionRequest(
            String.valueOf(taskId),
            String.valueOf(savedTestDefinition.getId()),
            codeEncoded
        );
        kafkaMessageProducer.sendTestDefinitionMessage(testDefinitionRequest);
        task.setOnTestChecking(true);
        taskService.save(task);
        return true;
    }

    public void saveTestDefinitionResponse(TestDefinitionResponse testDefinitionResponse) {
        TestDefinition testDefinition = testDefinitionRepo.findById(Long.valueOf(testDefinitionResponse.getRequestUuid())).get();
        String codeTest = null;
        if (!Objects.equals(testDefinitionResponse.getCode(), TestDefinitionResponseCode.TD_000.getCode())){
            codeTest = new String(java.util.Base64.getDecoder().decode(testDefinition.getCodeEncoded().getBytes(StandardCharsets.UTF_8)));
        }
        TestDefinitionResponseResult testDefinitionResponseResult = new TestDefinitionResponseResult(
                testDefinitionResponse.getCode(),
                testDefinitionResponse.getMessage(),
                codeTest,
                testDefinitionResponse.getValidationInfo(),
                testDefinitionResponse.getCompilationInfo(),
                testDefinitionResponse.getTechnicalErrorInfo()
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String testDefinitionResponseResultAsJson = objectMapper.writeValueAsString(testDefinitionResponseResult);
            testDefinition.setTestDefinitionResponseResultJson(testDefinitionResponseResultAsJson);
            testDefinition.setGettingResultTime(LocalDateTime.now());
            testDefinitionRepo.save(testDefinition);
            Task task = taskService.findById(Long.valueOf(testDefinitionResponse.getTaskId()));
            task.setOnTestChecking(false);
            taskService.save(task);
        } catch (JsonProcessingException e) {
            log.error("Exception during saving TestDefinitionResponse to database", e.getMessage());
        }
    }

    @Transactional
    public TestDefinitionResponseResult getTestDefinitionResponseResult(long taskId) {
        TestDefinition testDefinition = testDefinitionRepo.findLastByTask(taskService.findById(taskId));
        if (testDefinition == null ||testDefinition.getTestDefinitionResponseResultJson() == null) return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            TestDefinitionResponseResult testDefinitionResponseResult =
                    objectMapper.readValue(testDefinition.getTestDefinitionResponseResultJson(), TestDefinitionResponseResult.class);
            if (!testDefinition.isHasBeenAnalyzed()){
                testDefinition.setHasBeenAnalyzed(true);
                testDefinitionRepo.save(testDefinition);
            }
            return testDefinitionResponseResult;
        } catch (JsonProcessingException e){
            log.error("Exception during reading TestDefinitionResponseResult from JSON type to object", e.getMessage());
            return null;
        }
    }
}
