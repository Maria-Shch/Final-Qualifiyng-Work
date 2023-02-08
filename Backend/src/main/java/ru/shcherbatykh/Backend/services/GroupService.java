package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.repositories.GroupRepo;

import java.util.List;

@Service
public class GroupService {
    private final GroupRepo groupRepo;

    public GroupService(GroupRepo groupRepo) {
        this.groupRepo = groupRepo;
    }

    public List<Group> getGroups(){
        List<Group> groups = groupRepo.findAll();
        for (Group g: groups) g.getTeacher().setPassword("");
        return groups;
    }
}
