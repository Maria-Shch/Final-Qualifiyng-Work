package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.classes.ReasonOfProhibitionTesting;
import ru.shcherbatykh.Backend.dto.ResponseAboutTestingAllowed;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.TaskRepo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    public ResponseAboutTestingAllowed getResponseAboutTestingAllowed(int serialNumberOfChapter, int serialNumberOfBlock,
                                                                      int serialNumberOfTask, User user) {
        Task task = getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        Task prevTask = getPreviousTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
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
}
