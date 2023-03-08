package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Filter {
    private List<Long> groupIds;
    private List<Long> requestTypeIds;
    private List<Long> requestStateIds;
}
