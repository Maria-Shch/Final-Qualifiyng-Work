package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterRequests {
    private List<Long> groupIds;
    private List<Long> requestTypeIds;
    private List<Long> requestStateIds;
    private boolean ascending;
}
