package ru.shcherbatykh.Backend.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.shcherbatykh.Backend.classes.Role;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String lastname;
    private String patronymic;
    @Column(name = "email")
    private String username;
    private String password;
    @Transient
    private String confirmPassword;
    @Column(name = "registration_date")
    @CreationTimestamp
    private LocalDateTime registrationDate;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
