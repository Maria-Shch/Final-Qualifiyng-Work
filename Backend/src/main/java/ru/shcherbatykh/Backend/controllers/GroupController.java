package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.FilterGroups;
import ru.shcherbatykh.Backend.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.Backend.dto.NewGroupWithIdStudents;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.services.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;
    private final AuthService authService;
    private final YearService yearService;
    private final FacultyService facultyService;
    private final LevelOfEduService levelOfEduService;
    private final ProfileService profileService;
    private final FormOfEduService formOfEduService;
    private final UserService userService;

    public GroupController(GroupService groupService, AuthService authService, YearService yearService,
                           FacultyService facultyService, LevelOfEduService levelOfEduService,
                           ProfileService profileService, FormOfEduService formOfEduService, UserService userService) {
        this.groupService = groupService;
        this.authService = authService;
        this.yearService = yearService;
        this.facultyService = facultyService;
        this.levelOfEduService = levelOfEduService;
        this.profileService = profileService;
        this.formOfEduService = formOfEduService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<Group> getAllGroups() {
        return groupService.getGroups();
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
    @PostMapping("/all/forAdmin")
    public List<GroupWithUsersStatInfo> getGroupsByAdminAfterFiltering(@RequestBody FilterGroups filterGroups) {
        return groupService.getGroupsWithUsersByAdmin(filterGroups);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/levelsOfEdu")
    public List<LevelOfEdu> getLevelsOfEdu() {
        return levelOfEduService.getLevelsOfEduSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/profiles")
    public List<Profile> getProfiles() {
        return profileService.getProfilesSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/formsOfEdu")
    public List<FormOfEdu> getFormsOfEdu() {
        return formOfEduService.getFormsOfEduSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/quantityCourseNumber")
    public int getQuantityCourseNumber() {
        return groupService.getQuantityCourseNumber();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/quantityGroupNumber")
    public int getQuantityGroupNumber() {
        return groupService.getQuantityGroupNumber();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/maxQuantityOfProfiles")
    public int getMaxQuantityOfProfiles() {
        return profileService.getMaxQuantityOfProfiles();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/new/profile")
    public List<Profile> addNewProfile(@RequestBody Profile newProfile) {
        profileService.addNewProfile(newProfile);
        return profileService.getProfilesSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/new/faculty")
    public List<Faculty> addNewFaculty(@RequestBody Faculty newFaculty) {
        facultyService.addNewFaculty(newFaculty);
        return facultyService.getFacultiesSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/new/year")
    public List<Year> addNewYear(@RequestBody Year newYear) {
        yearService.addNewYear(newYear);
        return yearService.getYearsSorted();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/new")
    public Group addNewGroup(@RequestBody NewGroupWithIdStudents newGroupWithIdStudents) {
        Group group = groupService.addNewGroup(newGroupWithIdStudents.getGroup());
        userService.setGroup(newGroupWithIdStudents.getStudentIds(), group);
        return group;
    }
}
