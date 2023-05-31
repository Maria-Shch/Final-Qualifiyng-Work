package ru.shcherbatykh.application.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.classes.Role;
import ru.shcherbatykh.application.models.Group;
import ru.shcherbatykh.application.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
    Optional<User> getUserByUsername(String username);
    List<User> findAll();
    Optional<User> getUserByRole(Role role);
    List<User> findAllByGroup(Group group, Sort sort);
    List<User> findAllByRole(Role role);
    List<User> findByGroupIsNullAndRole(Role role, Sort sort);
}
