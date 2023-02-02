package ru.shcherbatykh.Backend.models;


import jakarta.persistence.*;

@Entity
@Table(name = "forms_of_edu")
public class FormOfEdu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
}
