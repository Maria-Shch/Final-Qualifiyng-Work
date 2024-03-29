package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterGroups {
    private List<Long> yearIds;
    private List<Long> facultyIds;
    private List<Long> teacherIds;
    private List<Long> levelOfEduIds;
    private List<Long> profileIds;
}
