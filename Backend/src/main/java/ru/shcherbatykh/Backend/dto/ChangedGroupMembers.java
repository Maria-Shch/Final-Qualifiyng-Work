package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChangedGroupMembers {
    List<Long> unselectedStudentsOfGroupIds;
    List<Long> selectedStudentsWithoutGroupIds;
}
