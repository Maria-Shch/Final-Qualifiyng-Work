package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.UserStatInfo;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.repositories.StudentTaskRepo;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentTaskService {
    private final StudentTaskRepo studentTaskRepo;
    private final StatusService statusService;
    private final TaskStatusesHistoryService taskStatusesHistoryService;

    public StudentTaskService(StudentTaskRepo studentTaskRepo, StatusService statusService, 
                              TaskStatusesHistoryService taskStatusesHistoryService) {
        this.studentTaskRepo = studentTaskRepo;
        this.statusService = statusService;
        this.taskStatusesHistoryService = taskStatusesHistoryService;
    }

    public StudentTask getStudentTask(User user, Task task){
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
        return studentTaskRepo.save(new StudentTask(user, task, statusService.getStatusByName("Не решена")));
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
}
