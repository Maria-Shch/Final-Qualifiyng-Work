package ru.shcherbatykh.application.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.utils.CommonUtils;

import javax.persistence.*;

@Entity
@Table(name = "groups")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_of_edu_id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private LevelOfEdu levelOfEdu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_of_edu_id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private FormOfEdu formOfEdu;

    private int courseNumber;
    private int groupNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id")
    private Year year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties(value = {"teacher", "hibernateLazyInitializer"})
    private User teacher;

    @Transient
    private String name;

    @PostLoad
    public void init() {
        name = CommonUtils.getGroupName(this);
    }
}
