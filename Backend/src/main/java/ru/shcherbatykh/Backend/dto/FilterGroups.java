package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterGroups {
    private List<Long> yearIds;
    private List<Long> facultyIds;
    private List<Long> teacherIds;
}
