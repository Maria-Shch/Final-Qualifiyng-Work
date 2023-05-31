package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.models.Group;

import java.util.List;

@Getter
@Setter
public class NewGroupWithIdStudents {
    Group group;
    List<Long> studentIds;
}
