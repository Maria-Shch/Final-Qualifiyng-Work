package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.shcherbatykh.Backend.classes.Role;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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

    public User(Long id, String name, String lastname, String patronymic, String username, String password) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.username = username;
        this.password = password;
    }
}
