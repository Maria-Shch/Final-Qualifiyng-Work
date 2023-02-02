package ru.shcherbatykh.Backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "levels_of_edu")
public class LevelOfEdu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
}
