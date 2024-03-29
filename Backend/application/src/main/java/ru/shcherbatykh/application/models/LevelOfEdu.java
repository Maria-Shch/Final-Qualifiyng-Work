package ru.shcherbatykh.application.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "levels_of_edu")
@Getter
@Setter
public class LevelOfEdu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String letter;
}
