package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.repositories.TaskRepo;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepo taskRepo;
    private final ChapterService chapterService;
    private final BlockService blockService;

    public TaskService(TaskRepo taskRepo, ChapterService chapterService, BlockService blockService) {
        this.taskRepo = taskRepo;
        this.chapterService = chapterService;
        this.blockService = blockService;
    }

    public List<TaskOfBlock> getPracticeForNoAuthUser(int serialNumberOfChapter, int serialNumberOfBlock){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        Block block = blockService.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
        List<Task> tasks = getTasksOfBlock(block);
        return tasks.stream()
                .map(t -> new TaskOfBlock(t.getId(), t.getSerialNumber(), t.getName(), null))
                .toList();
    }

    public List<Task> getTasksOfBlock(Block block){
        return taskRepo.getTaskByBlock(block);
    }
}
