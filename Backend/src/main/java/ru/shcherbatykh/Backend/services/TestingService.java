package ru.shcherbatykh.Backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.classes.AppError;
import ru.shcherbatykh.Backend.classes.ReasonOfProhibitionTesting;
import ru.shcherbatykh.Backend.dto.ResponseAboutTestingAllowed;
import ru.shcherbatykh.Backend.dto.TestingResultResponse;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;

import java.util.List;
import java.util.Objects;

@Service
public class TestingService {

    @Value("${app.testing.isTestsPassed}")
    private boolean IS_TESTS_PASSED;

    private final TaskService taskService;
    private final StudentTaskService studentTasksService;
    public TestingService(TaskService taskService, StudentTaskService studentTasksService) {
        this.taskService = taskService;
        this.studentTasksService = studentTasksService;
    }

    public ResponseAboutTestingAllowed getResponseAboutTestingAllowed(int serialNumberOfChapter, int serialNumberOfBlock,
                                                                      int serialNumberOfTask, User user) {
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

    public TestingResultResponse testing(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user, List<String> codes){
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        if(stTask == null){
            stTask = studentTasksService.addNew(user, task);
        }

        if (taskService.saveCodeToFiles(stTask, codes) == false){
            return new TestingResultResponse(stTask.getCurrStatus(),false, AppError.APP_ERR_001);
        } else {
            studentTasksService.setStatusOnTesting(stTask);
            if(IS_TESTS_PASSED){
                if(task.isManualCheckRequired()){
                    studentTasksService.setStatusPassedTests(stTask);
                } else {
                    studentTasksService.setStatusSolved(stTask);
                }
                return new TestingResultResponse(stTask.getCurrStatus(), true);
            } else {
                studentTasksService.setStatusNotPassedTests(stTask);
                return new TestingResultResponse(stTask.getCurrStatus(), false, AppError.APP_ERR_002);
            }
        }
    }
}
