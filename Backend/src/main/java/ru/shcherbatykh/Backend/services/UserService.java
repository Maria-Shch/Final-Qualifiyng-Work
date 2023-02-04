package ru.shcherbatykh.Backend.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.UserRepo;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<User> findByUsername(String username){
        return userRepo.getUserByUsername(username);
    }

    @Transactional
    public void addUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        //todo it's not thread safe when two users with the same username sign up at the same time
        userRepo.save(newUser);
    }

    public List<User> getUsers(){
        return userRepo.findAll();
    }
}
