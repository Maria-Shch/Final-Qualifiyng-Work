package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "request_types")
@Getter
@Setter
public class RequestType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public String name;
}
