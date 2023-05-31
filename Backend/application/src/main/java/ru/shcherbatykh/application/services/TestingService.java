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
import ru.shcherbatykh.application.models.CheckTest;
import ru.shcherbatykh.application.models.StudentTask;
import ru.shcherbatykh.application.models.Task;
import ru.shcherbatykh.application.models.User;
import ru.shcherbatykh.application.repositories.CheckTestRepo;
import ru.shcherbatykh.common.model.CodeCheckRequest;
import ru.shcherbatykh.common.model.CodeCheckResponse;
import ru.shcherbatykh.common.model.CodeSource;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TestingService {

    private final CheckTestRepo checkTestRepo;
    private final TaskService taskService;
    private final StudentTaskService studentTasksService;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final StatusService statusService;

    public TestingService(CheckTestRepo checkTestRepo, TaskService taskService, StudentTaskService studentTasksService,
                          KafkaMessageProducer kafkaMessageProducer, StatusService statusService) {
        this.checkTestRepo = checkTestRepo;
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
        CheckTest checkTest = checkTestRepo.findFirstByStudentTaskAndTeacherIsNull(stTask);
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
        CheckTest checkTest = checkTestRepo.findFirstByStudentTaskAndTeacher(stTask, teacher);
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
}
