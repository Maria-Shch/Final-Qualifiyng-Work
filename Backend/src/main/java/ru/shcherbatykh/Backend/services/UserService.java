package ru.shcherbatykh.Backend.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shcherbatykh.Backend.classes.Role;
import ru.shcherbatykh.Backend.dto.UserStatInfo;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.UserRepo;

import java.util.*;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final StudentTaskService studentTaskService;

    public UserService(UserRepo userRepo, @Lazy PasswordEncoder passwordEncoder, StudentTaskService studentTaskService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.studentTaskService = studentTaskService;
    }

    @Transactional
    public Optional<User> findByUsername(String username){
        return userRepo.getUserByUsername(username);
    }

    @Transactional
    public Optional<User> findById(Long id){
        return userRepo.findById(id);
    }

    @Transactional
    public User addUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        //todo it's not thread safe when two users with the same username sign up at the same time
        return userRepo.save(newUser);
    }

    public List<User> getUsers(){
        return userRepo.findAll();
    }

    public User getTeacher(StudentTask stTask){
        return getTeacher(stTask.getUser());
    }

    public User getTeacher(User student){
        if (student.getGroup() == null){
            return getAdmin();
        } else {
            return student.getGroup().getTeacher();
        }
    }

    public User getAdmin(){
        return userRepo.getUserByRole(Role.ADMIN).orElse(null);
    }

    public User updateEditableParams(User userWithNewParams) {
        boolean hasBeenUpdated = false;
        User oldUser = findById(userWithNewParams.getId()).orElse(null);
        if (oldUser != null){
            if (!Objects.equals(oldUser.getName(), userWithNewParams.getName())) {
                oldUser.setName(userWithNewParams.getName());
                hasBeenUpdated = true;
            }
            if (!Objects.equals(oldUser.getLastname(), userWithNewParams.getLastname())) {
                oldUser.setLastname(userWithNewParams.getLastname());
                hasBeenUpdated = true;
            }
            if (!Objects.equals(oldUser.getPatronymic(), userWithNewParams.getPatronymic())) {
                oldUser.setPatronymic(userWithNewParams.getPatronymic());
                hasBeenUpdated = true;
            }
            if (!Objects.equals(oldUser.getUsername(), userWithNewParams.getUsername())) {
                oldUser.setUsername(userWithNewParams.getUsername());
                hasBeenUpdated = true;
            }
        }

        if (hasBeenUpdated) {
            return userRepo.save(oldUser);
        } else return oldUser;
    }

    public List<User> getSortedUsersOfGroup(Group group){
        List<User> users = userRepo.findAllByGroup(group, orderByLastnameNamePatronymicAsc());
        for(User user: users){
            user.setPassword(null);
        }
        return users.stream()
                .sorted(Comparator.comparing(User::getLastname))
                .toList();
    }

    private Sort orderByLastnameNamePatronymicAsc() {
        return Sort.by(Sort.Direction.ASC, "lastname")
                .and(Sort.by(Sort.Direction.ASC, "name"))
                .and(Sort.by(Sort.Direction.ASC, "patronymic"));
    }

    public List<User> getTeachersSorted() {
        List<User> teachers = userRepo.findAllByRole(Role.TEACHER);
        Optional<User> admin = userRepo.getUserByRole(Role.ADMIN);
        admin.ifPresent(teachers::add);
        return teachers.stream()
                .sorted(Comparator.comparing(User::getLastname))
                .toList();
    }


    public List<UserStatInfo> getStudentsWithoutGroupWithStatInfo() {
        List<User> studentsWithoutGroup = userRepo.findByGroupIsNullAndRole(Role.USER);
        List<UserStatInfo> userStatInfoList = new ArrayList<>();
        for(User user: studentsWithoutGroup){
            userStatInfoList.add(studentTaskService.getUserStatInfo(user));
        }
        return userStatInfoList;
    }

    public List<User> getStudentsWithoutGroupSorted() {
        return userRepo.findByGroupIsNullAndRole(Role.USER);
    }


    public void setGroup(List<Long> studentIds, Group group) {
        for(Long id: studentIds){
            Optional<User> student = findById(id);
            student.ifPresent(user -> setGroupForStudent(user, group));
        }
    }

    public void setGroupForStudent(User student, Group group){
        student.setGroup(group);
        userRepo.save(student);
    }
}
