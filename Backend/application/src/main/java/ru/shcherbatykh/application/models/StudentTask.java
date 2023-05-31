package ru.shcherbatykh.application.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "student_tasks")
@Getter
@Setter
@NoArgsConstructor
public class StudentTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curr_status_id")
    private Status currStatus;

    public StudentTask(User user, Task task, Status currStatus) {
        this.user = user;
        this.task = task;
        this.currStatus = currStatus;
    }
}
