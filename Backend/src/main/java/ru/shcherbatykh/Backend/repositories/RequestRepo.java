package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Request;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepo extends PagingAndSortingRepository<Request, Long> {
    Request getRequestByStudentTaskAndClosingTime(StudentTask studentTask, LocalDateTime closingTime);

    List<Request> findAllByTeacher(User teacher, Pageable pageable);

    long countAllByTeacher(User teacher);
}
