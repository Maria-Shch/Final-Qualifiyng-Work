package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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
