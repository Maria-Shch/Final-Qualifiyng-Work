package ru.shcherbatykh.Backend.modelsForSend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFS {
    private long id;
    private String name;
    private String lastname;
    private String patronymic;
    private String username;

    public UserFS(long id, String name, String lastname, String patronymic, String username) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.username = username;
    }
}
