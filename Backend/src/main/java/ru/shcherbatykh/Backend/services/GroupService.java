package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.Backend.dto.UserStatInfo;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.GroupRepo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepo groupRepo;
    private final UserService userService;
    private final StudentTaskService studentTaskService;

    public GroupService(GroupRepo groupRepo, UserService userService, StudentTaskService studentTaskService) {
        this.groupRepo = groupRepo;
        this.userService = userService;
        this.studentTaskService = studentTaskService;
    }

    public List<Group> getGroups(){
        List<Group> groups = groupRepo.findAll();
        for (Group g: groups) g.getTeacher().setPassword("");
        return groups;
    }

    public List<Group> getGroupsByTeacher(User teacher) {
        List<Group> groups = groupRepo.findAllByTeacher(teacher);
        for (Group g: groups) g.getTeacher().setPassword("");
        return groups;
    }

    public List<GroupWithUsersStatInfo> getGroupsWithUsersByTeacher(User teacher) {
        List<Group> groups = getGroupsByTeacher(teacher).stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
        return convertGroupsToGroupsWithUsersStatInfo(groups);
    }

    public List<GroupWithUsersStatInfo> getGroupsWithUsersByAdmin() {
        List<Group> groups = getGroups().stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
        return convertGroupsToGroupsWithUsersStatInfo(groups);
    }

    public List<GroupWithUsersStatInfo> convertGroupsToGroupsWithUsersStatInfo(List<Group> groups){
        List<GroupWithUsersStatInfo> groupWithUsersStatInfos = new ArrayList<>();
        for(Group group: groups){
            GroupWithUsersStatInfo groupWithUsersStatInfo = new GroupWithUsersStatInfo();
            groupWithUsersStatInfo.setGroup(group);
            List<UserStatInfo> userStatInfos = new ArrayList<>();
            List<User> users = userService.getSortedUsersOfGroup(group);
            for(User user: users){
                userStatInfos.add(studentTaskService.getUserStatInfo(user));
            }
            groupWithUsersStatInfo.setUserStatInfos(userStatInfos);
            groupWithUsersStatInfos.add(groupWithUsersStatInfo);
        }
        return groupWithUsersStatInfos;
    }
}
