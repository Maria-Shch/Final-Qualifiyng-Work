package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.models.Group;

import java.util.List;

@Getter
@Setter
public class NewGroupWithIdStudents {
    Group group;
    List<Long> studentIds;
}
