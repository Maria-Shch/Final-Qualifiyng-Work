package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.models.User;
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
