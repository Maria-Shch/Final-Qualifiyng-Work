package ru.shcherbatykh.Backend.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.classes.AppError;
import ru.shcherbatykh.Backend.dto.RBOnConsideration;
import ru.shcherbatykh.Backend.dto.SendingOnReviewOrConsiderationResponse;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.repositories.TaskRepo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class TaskService {

    @Value("${app.codeStorage.path}")
    private String CODE_STORAGE_PATH;

    private final TaskRepo taskRepo;
    private final ChapterService chapterService;
    private final BlockService blockService;
    private final StudentTaskService studentTasksService;
    private final RequestService requestService;

    public TaskService(TaskRepo taskRepo, ChapterService chapterService, BlockService blockService,
                       StudentTaskService studentTasksService, RequestService requestService) {
        this.taskRepo = taskRepo;
        this.chapterService = chapterService;
        this.blockService = blockService;
        this.studentTasksService = studentTasksService;
        this.requestService = requestService;
    }

    public List<TaskOfBlock> getPracticeForNoAuthUser(int serialNumberOfChapter, int serialNumberOfBlock){
        return getTasks(serialNumberOfChapter,serialNumberOfBlock).stream()
                .map(t -> new TaskOfBlock(t.getId(), t.getSerialNumber(), t.getName(), null))
                .sorted(Comparator.comparing(TaskOfBlock::getSerialNumber))
                .toList();
    }

    public List<Task> getTasksOfBlock(Block block){
        return taskRepo.getTaskByBlock(block);
    }

    public List<Task> getTasks(int serialNumberOfChapter, int serialNumberOfBlock){
        Block block = blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock);
        return getTasksOfBlock(block);
    }

    public List<TaskOfBlock> getPracticeForAuthUser(int serialNumberOfChapter, int serialNumberOfBlock, User user){
        List<Task> tasks = getTasks(serialNumberOfChapter, serialNumberOfBlock);
        return tasks.stream()
                .map(t -> new TaskOfBlock(
                        t.getId(),
                        t.getSerialNumber(),
                        t.getName(),
                        studentTasksService.getStatusByUserAndTask(user, t)
                ))
                .sorted(Comparator.comparing(TaskOfBlock::getSerialNumber))
                .toList();
    }

    public int getCountOfTasks(int serialNumberOfChapter, int serialNumberOfBlock){
        Block block = blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock);
        return taskRepo.countByBlock(block);
    }

    public Task getTask(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask) {
        Block block = blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock);
        return taskRepo.getTasksByBlockAndSerialNumber(block, serialNumberOfTask);
    }

    public Task findById(Long taskId) {
        return taskRepo.findById(taskId).get();
    }

    public Task saveDescriptionOfTask(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, String description) {
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        task.setDescription(description);
        return taskRepo.save(task);
    }

    public Status getStatusOfTask(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user) {
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        return studentTasksService.getStatusByUserAndTask(user, task);
    }

    public Task getPreviousTask(int sNOfChapter, int sNOfBlock, int sNOfTask){
        if (sNOfTask == 1 && sNOfBlock == 1 && sNOfChapter == 1) return null;
        else if (sNOfTask != 1) {
            return getTask(sNOfChapter, sNOfBlock, sNOfTask - 1);
        }
        else if (sNOfBlock != 1 && sNOfTask == 1) {
            int sNOfLastTask = getLastTaskOfBlock(sNOfChapter, sNOfBlock - 1).getSerialNumber();
            return getTask (sNOfChapter, sNOfBlock - 1, sNOfLastTask);
        }
        else if (sNOfBlock == 1 && sNOfTask == 1){
            int sNOfPrevChapter = sNOfChapter - 1;
            int sNOfLastBlock = blockService.getLastBlockOfChapter(sNOfPrevChapter).getSerialNumber();
            int sNOfLastTask = getLastTaskOfBlock(sNOfPrevChapter, sNOfLastBlock).getSerialNumber();
            return getTask (sNOfPrevChapter, sNOfLastBlock, sNOfLastTask);
        }
        return null;
    }

    public Task getNextTask(int sNOfChapter, int sNOfBlock, int sNOfTask){
        if (sNOfTask == getLastTaskOfBlock(sNOfChapter, sNOfBlock).getSerialNumber()){
            if (sNOfBlock == blockService.getLastBlockOfChapter(sNOfChapter).getSerialNumber()){
                if (sNOfChapter == chapterService.getCountOfChapters()){
                    return null;
                }
                else return getTask(sNOfChapter + 1, 1, 1);
            } else {
                return getTask(sNOfChapter, sNOfBlock +  1, 1);
            }
        } else return getTask(sNOfChapter, sNOfBlock, sNOfTask + 1);
    }

    public Task getLastTaskOfBlock(int sNOfChapter, int sNOfBlock){
        return getTask(sNOfChapter, sNOfBlock, getCountOfTasks(sNOfChapter, sNOfBlock));
    }

    public SendingOnReviewOrConsiderationResponse sendTaskOnReview(int serialNumberOfChapter, int serialNumberOfBlock,
                                                                   int serialNumberOfTask, User user, List<String> codes){
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        if (!saveCodeToFiles(stTask, codes, true)){
            return new SendingOnReviewOrConsiderationResponse(stTask.getCurrStatus(),false, AppError.APP_ERR_001);
        } else {
            studentTasksService.setStatusOnReview(stTask);
            requestService.createRequestOnReview(stTask);
            return new SendingOnReviewOrConsiderationResponse(stTask.getCurrStatus(),true);
        }
    }

    public SendingOnReviewOrConsiderationResponse sendTaskOnConsideration(int serialNumberOfChapter, int serialNumberOfBlock,
                                                                          int serialNumberOfTask, User user,
                                                                          RBOnConsideration rbOnConsideration) {
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        if (!saveCodeToFiles(stTask, rbOnConsideration.getCodes(), true)){
            return new SendingOnReviewOrConsiderationResponse(stTask.getCurrStatus(),false, AppError.APP_ERR_001);
        } else {
            studentTasksService.setStatusOnConsideration(stTask);
            requestService.createRequestOnConsideration(stTask, rbOnConsideration.getMessage());
            return new SendingOnReviewOrConsiderationResponse(stTask.getCurrStatus(),true);
        }
    }

    public boolean saveCodeToFiles(StudentTask stTask, List<String> codes, boolean isStrategyOfSavingForStudent)  {
        String path = getPathToSave(stTask, isStrategyOfSavingForStudent);
        if (Files.exists(Path.of(path))) {
            try {
                FileUtils.cleanDirectory(new File(path));
            } catch (IOException e) {
                log.error("Error during saving code", e);
                return false;
            }
        }
        else {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                log.error("Error during saving code", e);
                return false;
            }
        }
        for(String code: codes){
            String className = getClassName();
            if(className == null) return false;
            String fullPath = path + "//" + className + ".txt";
            try {
                Files.writeString(Path.of(fullPath), code);
            } catch (IOException e) {
                log.error("Error during saving code", e);
                return false;
            }
        }
        return true;
    }

    private String getClassName(){
        return UUID.randomUUID().toString();
    }

    private String getPathToSave(StudentTask stTask, boolean isStrategyOfSavingForStudent){
        StringBuilder path = new StringBuilder(CODE_STORAGE_PATH);
        if (!isStrategyOfSavingForStudent){
            path.append('/').append("requestsCodes");
        }
        path.append('/')
                .append(stTask.getUser().getId()).append('/')
                .append(stTask.getTask().getBlock().getChapter().getSerialNumber()).append('/')
                .append(stTask.getTask().getBlock().getSerialNumber()).append('/')
                .append(stTask.getTask().getSerialNumber());
        return path.toString();
    }

    public List<String> getClassesForTask(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user){
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        return getClassesForTask(stTask, true);
    }

    public List<String> getClassesForTask(StudentTask stTask, boolean forUser){
        if (stTask == null) return null;
        String path = getPathToSave(stTask, forUser);
        if (Files.exists(Path.of(path))) {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(p -> {
                            try {
                                return Files.readString(p, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                log.error("Error during reading files", e);
                                return null;
                            }
                        })
                        .toList();
            } catch (IOException ex){
                log.error("Error during reading files", ex);
                return null;
            }
        }
        else {
            return null;
        }
    }

    public boolean arePresentCodesOfTeacher(StudentTask stTask) {
        if (stTask == null) return false;
        String path = getPathToSave(stTask, false);
        return Files.exists(Path.of(path));
    }

    public boolean cancelReview(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user) {
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        studentTasksService.setStatusPassedTests(stTask);
        requestService.cancelRequest(stTask);
        return true;
    }

    public boolean cancelConsideration(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask, User user) {
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        StudentTask stTask = studentTasksService.getStudentTask(user, task);
        studentTasksService.setStatusNotPassedTests(stTask);
        requestService.cancelRequest(stTask);
        return true;
    }

    public boolean setManualCheckValue(Long taskId, boolean manualCheckValue) {
        Task task = findById(taskId);
        task.setManualCheckRequired(manualCheckValue);
        taskRepo.save(task);
        return true;
    }
}
