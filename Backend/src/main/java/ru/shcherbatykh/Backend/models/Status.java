package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "task_statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}

