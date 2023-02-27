package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.TaskRepo;

import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepo taskRepo;
    private final ChapterService chapterService;
    private final BlockService blockService;
    private final StudentTasksService studentTasksService;

    public TaskService(TaskRepo taskRepo, ChapterService chapterService, BlockService blockService,
                       StudentTasksService studentTasksService) {
        this.taskRepo = taskRepo;
        this.chapterService = chapterService;
        this.blockService = blockService;
        this.studentTasksService = studentTasksService;
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
        Block block = blockService.getBlockBySNOfChapterAndSNOfBlock(serialNumberOfChapter, serialNumberOfBlock);
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
        Block block = blockService.getBlockBySNOfChapterAndSNOfBlock(serialNumberOfChapter, serialNumberOfBlock);
        return taskRepo.countByBlock(block);
    }

    public Task getTask(int serialNumberOfChapter, int serialNumberOfBlock, int serialNumberOfTask) {
        Block block = blockService.getBlockBySNOfChapterAndSNOfBlock(serialNumberOfChapter, serialNumberOfBlock);
        return taskRepo.getTasksByBlockAndSerialNumber(block, serialNumberOfTask);
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
}
