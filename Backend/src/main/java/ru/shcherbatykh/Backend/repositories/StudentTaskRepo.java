package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTaskRepo extends CrudRepository<StudentTask, Long> {
    Optional<StudentTask> findStudentTaskByUserAndTask(User user, Task task);

    int countAllByUserAndCurrStatus(User user, Status currStatus);

    List<StudentTask> findAll(Specification specification, Pageable pageable);

    List<StudentTask> findAll(Specification specification);

    int count(Specification specification);
}
