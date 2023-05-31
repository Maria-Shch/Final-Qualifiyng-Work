package ru.shcherbatykh.application.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.application.dto.ChangedGroupMembers;
import ru.shcherbatykh.application.dto.FilterGroups;
import ru.shcherbatykh.application.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.application.dto.NewGroupWithIdStudents;
import ru.shcherbatykh.application.models.*;
import ru.shcherbatykh.application.services.*;

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
    public List<Group> getGroupsByTeacher() {
        User teacher = authService.getUser().orElse(null);
        return groupService.getGroupsByTeacher(teacher);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/withUserStatInfo/all/forTeacher")
    public List<GroupWithUsersStatInfo> getGroupsWithUserStatInfoByTeacher() {
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
    @GetMapping("/withUserStatInfo/all/forAdmin")
    public List<GroupWithUsersStatInfo> getGroupsByAdmin() {
        return groupService.getGroupsWithUsersByAdmin();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/withUserStatInfo/all/forAdmin")
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
    @PostMapping("/create")
    public Group addNewGroup(@RequestBody NewGroupWithIdStudents newGroupWithIdStudents) {
        Group group = groupService.addNewGroup(newGroupWithIdStudents.getGroup());
        userService.setGroup(newGroupWithIdStudents.getStudentIds(), group);
        return group;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/{id}")
    public Group getGroupById(@PathVariable long id){
        return groupService.findById(id).orElse(null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/update")
    public Group updateGroup(@RequestBody Group updatedGroup) {
        return groupService.updateGroup(updatedGroup);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/updateMembers/{groupId}")
    public boolean updateGroupMembers(@PathVariable long groupId,
                                      @RequestBody ChangedGroupMembers changedGroupMembers) {
        return groupService.updateGroupMembers(groupId, changedGroupMembers);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/disband/{groupId}")
    public boolean disbandGroup(@PathVariable long groupId) {
        return groupService.disbandGroup(groupId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/delete/{groupId}")
    public boolean deleteGroup(@PathVariable long groupId) {
        return groupService.deleteGroup(groupId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/isPresent/year")
    public boolean checkPresentYearByName(@RequestBody String name) {
        return yearService.findByName(name).isPresent();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/isPresent/faculty")
    public boolean checkPresentFacultyByName(@RequestBody String name) {
        return facultyService.findByName(name).isPresent();
    }
}
