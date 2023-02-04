package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "closing_statuses")
public class ClosingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
