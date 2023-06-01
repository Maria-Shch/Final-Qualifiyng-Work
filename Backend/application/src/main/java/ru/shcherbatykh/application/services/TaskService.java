package ru.shcherbatykh.application.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.classes.AppError;
import ru.shcherbatykh.application.dto.*;
import ru.shcherbatykh.application.models.*;
import ru.shcherbatykh.application.repositories.PreviousTaskRepo;
import ru.shcherbatykh.application.repositories.TaskRepo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class TaskService {

    @Value("${app.codeStorage.path}")
    private String CODE_STORAGE_PATH;

    private final TaskRepo taskRepo;
    private final PreviousTaskRepo previousTaskRepo;
    private final ChapterService chapterService;
    private final BlockService blockService;
    private final StudentTaskService studentTasksService;
    private final RequestService requestService;

    public TaskService(TaskRepo taskRepo, PreviousTaskRepo previousTaskRepo, ChapterService chapterService,
                       BlockService blockService, StudentTaskService studentTasksService, RequestService requestService) {
        this.taskRepo = taskRepo;
        this.previousTaskRepo = previousTaskRepo;
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

    public List<Task> getSortedTasksOfBlock(Block block){
        return taskRepo.getTaskByBlock(block).stream()
                .sorted(Comparator.comparing(Task::getSerialNumber))
                .toList();
    }

    public List<Task> getTasks(int serialNumberOfChapter, int serialNumberOfBlock){
        Block block = blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock);
        return getSortedTasksOfBlock(block);
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
        //todo check
        else if (sNOfBlock != 1 && sNOfTask == 1) {
            Task lastTaskOfBlock;
            for (int i = 0; i < sNOfChapter; i++) {
                for (int j = 1; j < blockService.getCountOfBlocks(sNOfChapter - i); j++) {
                    lastTaskOfBlock = getLastTaskOfBlock(sNOfChapter-i, sNOfBlock - j);
                    if (lastTaskOfBlock != null){
                        return lastTaskOfBlock;
                    }
                }
            }
        }

        else if (sNOfBlock == 1 && sNOfTask == 1){
            Task lastTaskOfBlock;
            for (int i = 1; i < sNOfChapter; i++) {
                for (int j = 0; j < blockService.getCountOfBlocks(sNOfChapter - i); j++) {
                    lastTaskOfBlock = getLastTaskOfBlock(sNOfChapter-i, blockService.getCountOfBlocks(sNOfChapter-i)-j);
                    if (lastTaskOfBlock != null){
                        return lastTaskOfBlock;
                    }
                }
            }
        }
        return null;
    }

    public Task getNextTask(int sNOfChapter, int sNOfBlock, int sNOfTask){
        if (sNOfTask == getLastTaskOfBlock(sNOfChapter, sNOfBlock).getSerialNumber()){
            if (sNOfBlock == blockService.getLastBlockOfChapter(sNOfChapter).getSerialNumber()){
                if (sNOfChapter == chapterService.getCountOfChapters()){
                    return null;
                }
                else {
                    Task task;
                    for (int i = sNOfChapter + 1; i <= chapterService.getCountOfChapters(); i++) {
                        for (int j = 1; j <= blockService.getCountOfBlocks(i); j++) {
                            task = getTask(i, j, 1);
                            if (task != null){
                                return task;
                            }
                        }
                    }
                }
            } else {
                Task task;
                for (int i = sNOfBlock + 1; i <= blockService.getCountOfBlocks(sNOfChapter); i++) {
                    task = getTask(sNOfChapter, i, 1);
                    if (task != null){
                        return task;
                    }
                }

                for (int i = sNOfChapter + 1; i <= chapterService.getCountOfChapters(); i++) {
                    for (int j = 1; j <= blockService.getCountOfBlocks(i); j++) {
                        task = getTask(i, j, 1);
                        if (task != null){
                            return task;
                        }
                    }
                }
            }
        } else {
            return getTask(sNOfChapter, sNOfBlock, sNOfTask + 1);
        }
        return null;
    }

    public Task getLastTaskOfBlock(int sNOfChapter, int sNOfBlock){
        int countOfTasks = getCountOfTasks(sNOfChapter, sNOfBlock);
        if (countOfTasks != 0){
            return getTask(sNOfChapter, sNOfBlock, countOfTasks);
        } else {
            return null;
        }
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
                .append(stTask.getTask().getId());
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

    public boolean checkIsPresentNameOfTask(Task newTask) {
        return taskRepo.findTaskByBlockAndName(newTask.getBlock(), newTask.getName()) != null;
    }

    public Task createNewTask(NewTask newTask) {
        Task savedTask = taskRepo.save(newTask.getTask());
        for(Long taskId: newTask.getSelectedPreviousTaskIds()){
            Task task = findById(taskId);
            PreviousTask previousTask = new PreviousTask();
            previousTask.setTask(savedTask);
            previousTask.setPreviousTask(task);
            previousTaskRepo.save(previousTask);
        }
        return savedTask;
    }

    public SimpleCollection getSimpleTaskCollection() {
        SimpleCollection simpleCollection = new SimpleCollection();
        List<SimpleChapter> simpleChapterList = new ArrayList<>();
        simpleCollection.setSimpleChapterList(simpleChapterList);

        List<Chapter> chapters = chapterService.getChaptersSortBySerialNumber();
        for (Chapter chapter: chapters) {
            SimpleChapter simpleChapter = new SimpleChapter();
            simpleChapterList.add(simpleChapter);
            List<SimpleBlock> simpleBlockList = new ArrayList<>();
            simpleChapter.setSerialNumber(chapter.getSerialNumber());
            simpleChapter.setFullname("Глава " + chapter.getSerialNumber() + ". " + chapter.getName());
            simpleChapter.setSimpleBlockList(simpleBlockList);

            List<Block> blocks = blockService.getSortedBlocksOfChapterWithoutTheory(chapter.getSerialNumber());
            for(Block block: blocks){
                SimpleBlock simpleBlock = new SimpleBlock();
                simpleBlockList.add(simpleBlock);

                simpleBlock.setSerialNumber(block.getSerialNumber());
                simpleBlock.setFullname("Блок " + chapter.getSerialNumber() + ". " + block.getSerialNumber() + ". " + block.getName());

                List<SimpleTask> simpleTaskList = new ArrayList<>();
                simpleBlock.setSimpleTaskList(simpleTaskList);

                List<Task> tasks = getSortedTasksOfBlock(block);
                for(Task task: tasks){
                    SimpleTask simpleTask = new SimpleTask(
                            task.getId(),
                            task.getSerialNumber(),
                            "Задача " + chapter.getSerialNumber() + ". " + block.getSerialNumber() + ". " + task.getSerialNumber() + ". " + task.getName()
                    );
                    simpleTaskList.add(simpleTask);
                }
            }
        }
        return simpleCollection;
    }

    public Task getTaskById(long taskId) {
        return taskRepo.findById(taskId).get();
    }

    public Task updateTask(Task updatedTask) {
        Task task = taskRepo.findById(updatedTask.getId()).get();
        task.setName(updatedTask.getName());
        if (!Objects.equals(task.getBlock().getId(), updatedTask.getBlock().getId())){
            Block oldBlock = task.getBlock();
            int oldSerialNumber = task.getSerialNumber();
            int newSerialNumber = getCountOfTasks(updatedTask.getBlock().getChapter().getSerialNumber(),
                    updatedTask.getBlock().getSerialNumber())+1;
            task.setSerialNumber(newSerialNumber);
            task.setBlock(updatedTask.getBlock());
            task = taskRepo.save(task);

            if (oldSerialNumber - getCountOfTasks(oldBlock.getChapter().getSerialNumber(),
                    oldBlock.getSerialNumber()) != 1){
                if (getCountOfTasks(oldBlock.getChapter().getSerialNumber(),
                        oldBlock.getSerialNumber()) != 0){
                    for (int i = oldSerialNumber + 1; i <= getCountOfTasks(oldBlock.getChapter().getSerialNumber(),
                            oldBlock.getSerialNumber())+1; i++) {
                        Task t = getTask(oldBlock.getChapter().getSerialNumber(), oldBlock.getSerialNumber(), i);
                        int currSerialNumber = t.getSerialNumber();
                        t.setSerialNumber(currSerialNumber - 1);
                        taskRepo.save(t);
                    }
                }
            }
        } else {
            task = taskRepo.save(task);
        }
        return task;
    }

    public boolean updateTasksNumbering(RequestUpdateNumbering request) {
        for(NumberingPair pair: request.getNumberingPairs()){
            Task task = taskRepo.findById(pair.getObjId()).get();
            task.setSerialNumber(pair.getNewSerialNumber());
            taskRepo.save(task);
        }
        return true;
    }

    public Task save(Task task) {
        return taskRepo.save(task);
    }
}
