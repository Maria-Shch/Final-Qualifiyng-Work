package ru.shcherbatykh.application.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Task;
import ru.shcherbatykh.application.models.TestDefinition;

import java.util.Optional;

@Repository
public interface TestDefinitionRepo extends CrudRepository<TestDefinition, Long> {
    @Override
    Optional<TestDefinition> findById(Long id);

    @Query(
            value = "SELECT * FROM TEST_DEFINITIONS td WHERE td.task_id =?1 ORDER BY td.getting_result_time DESC NULLS LAST FETCH FIRST ROW ONLY",
            nativeQuery = true)
    TestDefinition findLastByTask(Task task);
}
