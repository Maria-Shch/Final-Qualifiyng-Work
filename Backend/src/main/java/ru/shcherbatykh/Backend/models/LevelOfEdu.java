package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "levels_of_edu")
public class LevelOfEdu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
