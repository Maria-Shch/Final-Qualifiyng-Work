package ru.shcherbatykh.Backend.models;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_task_id")
    private StudentTask studentTask;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private RequestType requestType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private RequestState requestState;
    private String studentMsg;
    @Column(name = "creation_time")
    @CreationTimestamp
    private LocalDateTime creationTime;
    private String teacherMsg;
    @Column(name = "closing_time")
    private LocalDateTime closingTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closing_status_id")
    private ClosingStatus closingStatus;
}
