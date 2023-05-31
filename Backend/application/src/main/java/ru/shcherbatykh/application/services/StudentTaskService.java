package ru.shcherbatykh.application.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shcherbatykh.application.dto.BlockAndStatInfo;
import ru.shcherbatykh.application.dto.ChapterAndStatInfo;
import ru.shcherbatykh.application.dto.StudentProgress;
import ru.shcherbatykh.application.dto.UserStatInfo;
import ru.shcherbatykh.application.models.*;
import ru.shcherbatykh.application.repositories.StudentTaskRepo;
import ru.shcherbatykh.application.repositories.TaskRepo;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StudentTaskService {
    private final TaskRepo taskRepo;
    private final StudentTaskRepo studentTaskRepo;
    private final StatusService statusService;
    private final TaskStatusesHistoryService taskStatusesHistoryService;
    private final ChapterService chapterService;
    private final BlockService blockService;

    public StudentTaskService(TaskRepo taskRepo, StudentTaskRepo studentTaskRepo, StatusService statusService,
                              TaskStatusesHistoryService taskStatusesHistoryService, ChapterService chapterService,
                              BlockService blockService) {
        this.taskRepo = taskRepo;
        this.studentTaskRepo = studentTaskRepo;
        this.statusService = statusService;
        this.taskStatusesHistoryService = taskStatusesHistoryService;
        this.chapterService = chapterService;
        this.blockService = blockService;
    }

    @Transactional
    public StudentTask getStudentTask(User user, Task task){
        studentTaskRepo.lockTable();
        StudentTask stTask = studentTaskRepo.findStudentTaskByUserAndTask(user, task).orElse(null);
        if (stTask == null){
            stTask = addNew(user, task);
        }
        return stTask;
    }

    public StudentTask findById(Long id){
       return studentTaskRepo.findById(id).orElse(null);
    }

    public StudentTask addNew(User user, Task task){
        try {
            return studentTaskRepo.save(new StudentTask(user, task, statusService.getStatusByName("Не решена")));
        } catch (DataIntegrityViolationException e){
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause instanceof SQLException sqlException && "23505".equals(sqlException.getSQLState())){
                log.error("Error during save StudentTask", e);
                return getStudentTask(user, task);
            } else {
                throw e;
            }
        }
    }

    public Status getStatusByUserAndTask(User user, Task task){
        Optional<StudentTask> studentTask = studentTaskRepo.findStudentTaskByUserAndTask(user, task);
        if (studentTask.isPresent()) return studentTask.get().getCurrStatus();
        else return statusService.getStatusByName("Не решена");
    }

    private void setStatusAndWriteHistory(StudentTask sT, String status){
        Status oldStatus = sT.getCurrStatus();
        Status newStatus = statusService.getStatusByName(status);
        sT.setCurrStatus(newStatus);
        studentTaskRepo.save(sT);
        taskStatusesHistoryService.registerStatusChange(sT, oldStatus, newStatus);
    }

    public void setStatusNotSolved(StudentTask sT){
        setStatusAndWriteHistory(sT, "Не решена");
    }

    public void setStatusOnReview(StudentTask sT){
        setStatusAndWriteHistory(sT, "На проверке");
    }

    public void setStatusRejected(StudentTask sT){
        setStatusAndWriteHistory(sT, "Возвращена преподавателем");
    }

    public void setStatusSolved(StudentTask sT){
        setStatusAndWriteHistory(sT, "Решена");
    }

    public void setStatusOnTesting(StudentTask sT){
        setStatusAndWriteHistory(sT, "На тестировании");
    }

    public void setStatusNotPassedTests(StudentTask sT){
        setStatusAndWriteHistory(sT, "Не прошла тесты");
    }

    public void setStatusPassedTests(StudentTask sT){
        setStatusAndWriteHistory(sT, "Прошла тесты");
    }

    public void setStatusOnConsideration(StudentTask sT){
        setStatusAndWriteHistory(sT, "На рассмотрении");
    }

    public UserStatInfo getUserStatInfo(User user){
        return new UserStatInfo(user, getLastSolvedTask(user), getCountOfSolvedTasks(user));
    }

    public int getCountOfSolvedTasks(User user){
        return studentTaskRepo.countAllByUserAndCurrStatus(user, statusService.getStatusByName("Решена"));
    }

    public Task getLastSolvedTask(User user){
        Specification<?> specification = getSpecificationForLastSolvedTask(user.getId(),
                statusService.getStatusByName("Решена").getId());
        List<StudentTask> studentTasks = studentTaskRepo.findAll(specification, PageRequest.ofSize(1));
        Optional<StudentTask> lastSolvedStudentTask = studentTasks.stream().findAny();
        return lastSolvedStudentTask.map(StudentTask::getTask).orElse(null);
    }

    private Specification<StudentTask> getSpecificationForLastSolvedTask(Long userId, Long statusId){
        return (root, query, criteriaBuilder) -> {
            Join<Task, StudentTask> task = root.join("task");
            Join<Block, Join<Task, StudentTask>> block = task.join("block");
            Join<Chapter, Join<Block, Join<Task, StudentTask>>> chapter = block.join("chapter");
            Predicate user = criteriaBuilder.equal(root.get("user"), userId);
            Predicate status = criteriaBuilder.equal(root.get("currStatus"), statusId);
            Predicate predicate = criteriaBuilder.and(user, status);
            query.orderBy(criteriaBuilder.desc(chapter.get("serialNumber")),
                    criteriaBuilder.desc(block.get("serialNumber")),
                    criteriaBuilder.desc(task.get("serialNumber")));
            return predicate;
        };
    }

    public StudentProgress getStudentProgress(User user){
        StudentProgress studentProgress = new StudentProgress();
        List<ChapterAndStatInfo> chapterAndStatInfoList = new ArrayList<>();

        List<Chapter> chapters = chapterService.getChaptersSortBySerialNumber();
        for (Chapter chapter: chapters){
            ChapterAndStatInfo chapterAndStatInfo = new ChapterAndStatInfo();
            chapterAndStatInfo.setChapter(chapter);
            chapterAndStatInfo.setCountOfSolvedTasks(getCountSolvedTasksByUserAndChapter(user, chapter));
            chapterAndStatInfo.setCountOfAllTasks(getCountTasksByChapter(chapter));
            List<BlockAndStatInfo> blockAndStatInfoList = new ArrayList<>();

            List<Block> blocks = blockService.getSortedBlocksOfChapterWithoutTheory(chapter.getSerialNumber());
            for(Block block: blocks){
                BlockAndStatInfo blockAndStatInfo = new BlockAndStatInfo();
                blockAndStatInfo.setBlock(block);
                blockAndStatInfo.setCountOfSolvedTasks(getCountSolvedTasksByUserAndBlock(user, block));
                blockAndStatInfo.setCountOfAllTasks(taskRepo.countByBlock(block));

                //todo clear user info
                List<StudentTask> studentTaskList = getStudentTasksByUserAndBlock(user, block);
                blockAndStatInfo.setStudentTaskList(studentTaskList);

                blockAndStatInfoList.add(blockAndStatInfo);
            }
            chapterAndStatInfo.setBlockAndStatInfoList(blockAndStatInfoList);

            chapterAndStatInfoList.add(chapterAndStatInfo);
        }
        studentProgress.setChapterAndStatInfoList(chapterAndStatInfoList);
        return studentProgress;
    }

    public List<StudentTask> getStudentTasksByUserAndBlock(User user, Block block){
        List<StudentTask> presentStudentTasks = studentTaskRepo.findAll(getSpecificationByUserAndBlock(user.getId(), block.getId()));
        List<Long> taskIdsOfPresentStudentTasks = presentStudentTasks.stream()
                .map(studentTask -> studentTask.getTask().getId())
                .toList();
        if (presentStudentTasks.size() != blockService.getCountOfBlocks(block.getChapter().getSerialNumber())){
            List<Task> tasksOfBlock = taskRepo.getTaskByBlock(block);
            for (Task task: tasksOfBlock){
                if (!taskIdsOfPresentStudentTasks.contains(task.getId())){
                    StudentTask nonexistentStudentTask = new StudentTask(user, task, statusService.getStatusByName("Не решена"));
                    presentStudentTasks.add(nonexistentStudentTask);
                }
            }
        }
        return presentStudentTasks;
    }

    private Specification<StudentTask> getSpecificationByUserAndBlock(Long userId, Long blockId){
        return (root, query, criteriaBuilder) -> {
            Join<Task, StudentTask> task = root.join("task");
            Join<Block, Join<Task, StudentTask>> block = task.join("block");
            Predicate user = criteriaBuilder.equal(root.get("user"), userId);
            Predicate blockPr = criteriaBuilder.equal(task.get("block"), blockId);
            Predicate predicate = criteriaBuilder.and(user, blockPr);
            query.orderBy(criteriaBuilder.asc(block.get("serialNumber")),
                    criteriaBuilder.asc(task.get("serialNumber")));
            return predicate;
        };
    }

    public int getCountSolvedTasksByUserAndBlock(User user, Block block){
        return studentTaskRepo.count(getSpecificationForSolvedTasksByUserAndBlock(user.getId(), block.getId(),
                statusService.getStatusByName("Решена").getId()));
    }

    private Specification<StudentTask> getSpecificationForSolvedTasksByUserAndBlock(Long userId, Long blockId, Long statusId){
        return (root, query, criteriaBuilder) -> {
            Join<Task, StudentTask> task = root.join("task");
            Join<Block, Join<Task, StudentTask>> block = task.join("block");
            Predicate user = criteriaBuilder.equal(root.get("user"), userId);
            Predicate status = criteriaBuilder.equal(root.get("currStatus"), statusId);
            Predicate blockPr = criteriaBuilder.equal(task.get("block"), blockId);
            Predicate predicate = criteriaBuilder.and(user, status, blockPr);
            query.orderBy(criteriaBuilder.asc(block.get("serialNumber")),
                    criteriaBuilder.asc(task.get("serialNumber")));
            return predicate;
        };
    }

    public int getCountSolvedTasksByUserAndChapter(User user, Chapter chapter){
        return studentTaskRepo.count(getSpecificationForSolvedTasksByUserAndChapter(user.getId(), chapter.getId(),
                statusService.getStatusByName("Решена").getId()));
    }

    private Specification<StudentTask> getSpecificationForSolvedTasksByUserAndChapter(Long userId, Long chapterId, Long statusId){
        return (root, query, criteriaBuilder) -> {
            Join<Task, StudentTask> task = root.join("task");
            Join<Block, Join<Task, StudentTask>> block = task.join("block");
            Join<Chapter, Join<Block, Join<Task, StudentTask>>> chapter = block.join("chapter");
            Predicate user = criteriaBuilder.equal(root.get("user"), userId);
            Predicate status = criteriaBuilder.equal(root.get("currStatus"), statusId);
            Predicate blockPr = criteriaBuilder.equal(block.get("chapter"), chapterId);
            Predicate predicate = criteriaBuilder.and(user, status, blockPr);
            query.orderBy(criteriaBuilder.asc(chapter.get("serialNumber")),
                    criteriaBuilder.asc(block.get("serialNumber")),
                    criteriaBuilder.asc(task.get("serialNumber")));
            return predicate;
        };
    }

    public int getCountTasksByChapter(Chapter chapter){
        return taskRepo.count(getSpecificationTasksByChapter(chapter.getId()));
    }

    private Specification<Task> getSpecificationTasksByChapter(Long chapterId){
        return (root, query, criteriaBuilder) -> {
            Join<Block, Task> block = root.join("block");
            Join<Chapter, Join<Block, Task>> chapter = block.join("chapter");
            Predicate chapterIdPr = criteriaBuilder.equal(block.get("chapter"), chapterId);
            Predicate predicate = criteriaBuilder.and(chapterIdPr);
            return predicate;
        };
    }
}
