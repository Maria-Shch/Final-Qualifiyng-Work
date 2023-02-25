package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_statuses_history")
@Getter
@Setter
public class TaskStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_task_id")
    private StudentTask studentTask;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_status_id")
    private Status oldStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_status_id")
    private Status newStatus;
    @Column(name = "change_time")
    @CreationTimestamp
    private LocalDateTime changeTime;
}
