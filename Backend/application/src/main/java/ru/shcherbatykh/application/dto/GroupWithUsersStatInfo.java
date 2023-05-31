package ru.shcherbatykh.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shcherbatykh.application.models.Group;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupWithUsersStatInfo {
    private Group group;
    private List<UserStatInfo> userStatInfos;
}
