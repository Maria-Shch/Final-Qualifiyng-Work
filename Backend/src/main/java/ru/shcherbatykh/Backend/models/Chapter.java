package ru.shcherbatykh.Backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int serialNumber;
    private String name;
}
