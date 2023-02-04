package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "request_states")
public class RequestState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
