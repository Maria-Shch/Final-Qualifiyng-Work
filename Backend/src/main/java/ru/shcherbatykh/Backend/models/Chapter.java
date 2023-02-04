package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int serialNumber;
    private String name;
}
