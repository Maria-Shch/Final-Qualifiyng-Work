package ru.shcherbatykh.Backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.GroupService;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;
    private final AuthService authService;

    public GroupController(GroupService groupService, AuthService authService) {
        this.groupService = groupService;
        this.authService = authService;
    }

    @GetMapping("/all")
    public List<Group> getAllGroups(){
        return groupService.getGroups().stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    @GetMapping("/all/forTeacher")
    public List<GroupWithUsersStatInfo> getGroupsByTeacher(){
        User teacher = authService.getUser().orElse(null);
        return groupService.getGroupsWithUsersByTeacher(teacher);
    }
}
