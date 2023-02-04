package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "logically_related_previous_tasks")
public class PreviousTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_task_id")
    private Task previousTask;
}
