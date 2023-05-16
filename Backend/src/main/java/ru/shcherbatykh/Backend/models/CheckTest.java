package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_tests")
@Getter
@Setter
@NoArgsConstructor
public class CheckTest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_task_id")
    private StudentTask studentTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User teacher;

    @Lob
    private String codeCheckResponseResultJson;

    @Column(name = "getting_result_time")
    private LocalDateTime gettingResultTime;

    public CheckTest(Long id, StudentTask studentTask, User teacher) {
        this.id = id;
        this.studentTask = studentTask;
        this.teacher = teacher;
    }
}
