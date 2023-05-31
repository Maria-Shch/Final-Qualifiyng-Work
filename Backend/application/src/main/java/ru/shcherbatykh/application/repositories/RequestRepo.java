package ru.shcherbatykh.application.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Request;
import ru.shcherbatykh.application.models.RequestState;
import ru.shcherbatykh.application.models.StudentTask;
import ru.shcherbatykh.application.models.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepo extends PagingAndSortingRepository<Request, Long> {
    Request getRequestByStudentTaskAndClosingTime(StudentTask studentTask, LocalDateTime closingTime);

    List<Request> findAllByTeacher(User teacher, Pageable pageable);

    long countAllByTeacher(User teacher);

    int count(Specification<Request> specification);

    List<Request> findAll(Specification<Request> specification, Pageable pageable);

    List<Request> findAll(Specification<Request> specification);

    List<Request> findAllByTeacherAndRequestStateIn(User teacher, List<RequestState> requestStates);
}
