package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;

import java.util.Optional;

@Repository
public interface StudentTaskRepo extends CrudRepository<StudentTask, Long> {
    Optional<StudentTask> findStudentTaskByUserAndTask(User user, Task task);
}
