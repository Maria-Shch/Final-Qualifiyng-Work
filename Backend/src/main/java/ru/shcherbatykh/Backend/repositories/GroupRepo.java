package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.models.User;

import java.util.List;

@Repository
public interface GroupRepo extends CrudRepository<Group, Long> {
    List<Group> findAll();
    List<Group> findAll(Specification<Group> specification);
    List<Group> findAllByTeacher(User teacher);
}
