package ru.shcherbatykh.Backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.services.GroupService;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/all")
    public List<Group> getGroup(){
        return groupService.getGroups().stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }
}
