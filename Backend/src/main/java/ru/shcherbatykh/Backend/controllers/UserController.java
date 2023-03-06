package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/get")
    public User getUser(){
        return authService.getUser().orElse(null);
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
}
