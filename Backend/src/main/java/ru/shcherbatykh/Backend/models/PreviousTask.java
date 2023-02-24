package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "logically_related_previous_tasks")
@Getter
@Setter
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
