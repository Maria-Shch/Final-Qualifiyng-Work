package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Request;
import ru.shcherbatykh.Backend.models.StudentTask;

import java.time.LocalDateTime;

@Repository
public interface RequestRepo extends CrudRepository<Request, Long> {
    Request getRequestByStudentTaskAndClosingTime(StudentTask studentTask, LocalDateTime closingTime);
}
