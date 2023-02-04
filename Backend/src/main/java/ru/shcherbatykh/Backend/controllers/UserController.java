package ru.shcherbatykh.Backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.modelsForSend.UserFS;
import ru.shcherbatykh.Backend.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<UserFS> getUsers(){
        return userService.getUsers()
                .stream()
                .map(u -> new UserFS(u.getId(), u.getName(), u.getLastname(), u.getPatronymic(), u.getUsername())).toList();
    }
}
