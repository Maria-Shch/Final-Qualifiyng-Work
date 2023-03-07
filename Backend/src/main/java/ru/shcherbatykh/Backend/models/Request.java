package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.shcherbatykh.Backend.utils.CommonUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
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
    @Column(name = "student_message")
    private String studentMsg;
    @Column(name = "creation_time")
    @CreationTimestamp
    private LocalDateTime creationTime;
    @Column(name = "teacher_message")
    private String teacherMsg;
    @Column(name = "closing_time")
    private LocalDateTime closingTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closing_status_id")
    private ClosingStatus closingStatus;

    @Transient
    private String creationTimeToPrint;

    public Request(StudentTask studentTask, User teacher, RequestType requestType, RequestState requestState) {
        this.studentTask = studentTask;
        this.teacher = teacher;
        this.requestType = requestType;
        this.requestState = requestState;
    }

    public Request(StudentTask studentTask, User teacher, RequestType requestType, RequestState requestState,
                   String studentMsg) {
        this.studentTask = studentTask;
        this.teacher = teacher;
        this.requestType = requestType;
        this.requestState = requestState;
        this.studentMsg = studentMsg;
    }


    @PostLoad
    public void init() {
        creationTimeToPrint = CommonUtils.getCreationTimeToPrint(creationTime);
    }
}
