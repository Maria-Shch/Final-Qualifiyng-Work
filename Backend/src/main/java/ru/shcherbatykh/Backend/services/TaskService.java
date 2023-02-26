package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.TaskRepo;

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
                .toList();
    }

    public List<Task> getTasksOfBlock(Block block){
        return taskRepo.getTaskByBlock(block);
    }

    public List<Task> getTasks(int serialNumberOfChapter, int serialNumberOfBlock){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        Block block = blockService.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
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
                )).toList();
    }

    public Block saveTextOfTheory(int serialNumberOfChapter, int serialNumberOfBlock, String textOfTheory){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        Block block = blockService.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
        block.setTextTheory(textOfTheory);
        return blockService.saveBlock(block);
    }
}
