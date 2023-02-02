package ru.shcherbatykh.Backend.models;

import jakarta.persistence.*;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int serialNumber;
    private String name;
    private String description;
    private boolean manualCheckRequired;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @ToString.Exclude
    private List<PreviousTask> previousTasks = new ArrayList<>();
}
