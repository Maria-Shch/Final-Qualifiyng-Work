package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "forms_of_edu")
public class FormOfEdu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
