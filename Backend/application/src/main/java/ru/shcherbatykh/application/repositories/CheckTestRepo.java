package ru.shcherbatykh.application.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.CheckTest;
import ru.shcherbatykh.application.models.StudentTask;
import ru.shcherbatykh.application.models.User;

import java.util.Optional;

@Repository
public interface CheckTestRepo extends CrudRepository<CheckTest, Long> {
    @Override
    Optional<CheckTest> findById(Long id);

    @Query(
            value = "SELECT * FROM CHECK_TESTS ct WHERE ct.user_id IS NULL and ct.student_task_id = ?1 ORDER BY ct.getting_result_time DESC NULLS LAST FETCH FIRST ROW ONLY",
            nativeQuery = true)
    CheckTest findLastByStudentTaskAndTeacherIsNull(StudentTask studentTask);

    @Query(
            value = "SELECT * FROM CHECK_TESTS ct WHERE ct.student_task_id = ?1 and ct.user_id = ?2 ORDER BY ct.getting_result_time DESC NULLS LAST FETCH FIRST ROW ONLY",
            nativeQuery = true)
    CheckTest findLastByStudentTaskAndTeacher(StudentTask studentTask, User teacher);
}
