package ru.shcherbatykh.Backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "task_statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
}

