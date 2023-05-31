package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RBOnConsideration {
    List<String> codes;
    String message;
}
