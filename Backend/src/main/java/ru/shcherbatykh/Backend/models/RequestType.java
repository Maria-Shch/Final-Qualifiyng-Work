package ru.shcherbatykh.Backend.models;

import javax.persistence.*;

@Entity
@Table(name = "request_types")
public class RequestType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public String name;
}
