package ru.shcherbatykh.application.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Group;
import ru.shcherbatykh.application.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepo extends CrudRepository<Group, Long> {
    List<Group> findAll();
    List<Group> findAll(Specification<Group> specification);
    List<Group> findAllByTeacher(User teacher);
    Optional<Group> findById(Long id);
}
