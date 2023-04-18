package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.Backend.dto.UserStatInfo;
import ru.shcherbatykh.Backend.models.Faculty;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.models.Year;
import ru.shcherbatykh.Backend.services.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;
    private final AuthService authService;
    private final YearService yearService;
    private final FacultyService facultyService;
    private final UserService userService;

    public GroupController(GroupService groupService, AuthService authService, YearService yearService,
                           FacultyService facultyService, UserService userService) {
        this.groupService = groupService;
        this.authService = authService;
        this.yearService = yearService;
        this.facultyService = facultyService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<Group> getAllGroups() {
        return groupService.getGroups().stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/all/forTeacher")
    public List<GroupWithUsersStatInfo> getGroupsByTeacher() {
        User teacher = authService.getUser().orElse(null);
        return groupService.getGroupsWithUsersByTeacher(teacher);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/years")
    public List<Year> getYears() {
        return yearService.getYearsSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/faculties")
    public List<Faculty> getFaculties() {
        return facultyService.getFacultiesSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/all/forAdmin")
    public List<GroupWithUsersStatInfo> getGroupsByAdmin() {
        return groupService.getGroupsWithUsersByAdmin();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/all/studentsWithoutGroup/forAdmin")
    public List<UserStatInfo> getStudentsWithoutGroupWithStatInfo() {
        return userService.getStudentsWithoutGroupWithStatInfo();
    }
}
