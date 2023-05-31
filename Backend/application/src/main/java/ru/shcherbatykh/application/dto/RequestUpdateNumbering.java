package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestUpdateNumbering {
    List<NumberingPair> numberingPairs;
}
