package ru.shcherbatykh.application.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_definitions")
@Getter
@Setter
@NoArgsConstructor
public class TestDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Lob
    @Column(name = "code_encoded")
    private String codeEncoded;

    @Lob
    @Column(name = "test_definition_response_result_json")
    private String testDefinitionResponseResultJson;

    @Column(name = "getting_result_time")
    private LocalDateTime gettingResultTime;

    @Column(name = "has_been_analyzed")
    private boolean hasBeenAnalyzed;

    public TestDefinition(Task task, String codeEncoded) {
        this.id = null;
        this.task = task;
        this.codeEncoded = codeEncoded;
    }
}
