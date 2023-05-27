package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimpleChapter {
    int serialNumber;
    String fullname;
    List<SimpleBlock> simpleBlockList;
}
