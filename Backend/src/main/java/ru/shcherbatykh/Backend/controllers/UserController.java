package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.UserStatInfo;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.GroupService;
import ru.shcherbatykh.Backend.services.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final GroupService groupService;

    public UserController(UserService userService, AuthService authService, GroupService groupService) {
        this.userService = userService;
        this.authService = authService;
        this.groupService = groupService;
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/get")
    public User getUser(){
        Optional<User> user = authService.getUser();
        if(user.isPresent()) {
            user.get().setPassword(null);
            return user.get();
        } return null;
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @PostMapping("/updateEditable")
    public User updateEditable(@RequestBody User user) {
        return userService.updateEditableParams(user);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/teacher")
    public User getTeacher(){
        User user = authService.getUser().orElse(null);
        return userService.getTeacher(user);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/admin")
    public User getAdmin(){
        return userService.getAdmin();
    }

    @GetMapping("/all")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping("/isPresent")
    public boolean isPresentUser (@RequestBody String username) {
        return userService.findByUsername(username).isPresent();
    }

    @PostMapping(value = "/registerNewUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User registerNewUser (@RequestBody User newUser) {
        return userService.addUser(newUser);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable long id){
        return userService.findById(id).orElse(null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/teachers")
    public List<User> getTeachers() {
        return userService.getTeachersSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/studentsWithoutGroupWithStatInfo/forAdmin")
    public List<UserStatInfo> getStudentsWithoutGroupWithStatInfo() {
        return userService.getStudentsWithoutGroupWithStatInfo();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/studentsWithoutGroup")
    public List<User> getStudentsWithoutGroup() {
        return userService.getStudentsWithoutGroupSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/get/all/byGroupId/{id}")
    public List<User> getStudentsByGroupId(@PathVariable long id){
        return userService.getSortedUsersOfGroup(groupService.findById(id).get());
    }
}
