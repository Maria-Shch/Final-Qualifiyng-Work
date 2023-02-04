package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "student_tasks")
public class StudentTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curr_status_id")
    private Status currStatus;
}
