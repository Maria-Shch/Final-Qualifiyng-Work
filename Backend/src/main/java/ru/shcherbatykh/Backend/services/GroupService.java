package ru.shcherbatykh.Backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.shcherbatykh.Backend.dto.FilterGroups;
import ru.shcherbatykh.Backend.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.Backend.dto.UserStatInfo;
import ru.shcherbatykh.Backend.models.Faculty;
import ru.shcherbatykh.Backend.models.Group;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.models.Year;
import ru.shcherbatykh.Backend.repositories.GroupRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepo groupRepo;
    private final UserService userService;
    private final StudentTaskService studentTaskService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.group.quantity.course-number}")
    private int quantityCourseNumber;

    @Value("${app.group.quantity.group-number}")
    private int quantityGroupNumber;

    public GroupService(GroupRepo groupRepo, UserService userService, StudentTaskService studentTaskService) {
        this.groupRepo = groupRepo;
        this.userService = userService;
        this.studentTaskService = studentTaskService;
    }

    public List<Group> getGroups(){
        List<Group> groups = groupRepo.findAll();
        for (Group g: groups) g.getTeacher().setPassword("");
        return groups.stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    public List<Group> getGroups(Specification<Group> specification){
        List<Group> groups = groupRepo.findAll(specification);
        for (Group g: groups) g.getTeacher().setPassword("");
        return groups.stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    public List<Group> getGroupsByTeacher(User teacher) {
        List<Group> groups = groupRepo.findAllByTeacher(teacher);
        for (Group g: groups) g.getTeacher().setPassword("");
        return groups.stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
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

    public List<GroupWithUsersStatInfo> getGroupsWithUsersByAdmin(FilterGroups filterGroups) {
        List<Group> groups = getGroups(getSpecification(filterGroups));
        return convertGroupsToGroupsWithUsersStatInfo(groups);
    }

    private Specification<Group> getSpecification(FilterGroups filterGroups) {

        Specification<Group> specification = null;

        if (!CollectionUtils.isEmpty(filterGroups.getYearIds())) {
            specification = hasYearIds(filterGroups.getYearIds());
        }

        if (!CollectionUtils.isEmpty(filterGroups.getFacultyIds())) {
            if (specification == null) {
                specification = hasFacultyIds(filterGroups.getFacultyIds());
            } else{
                specification = specification.and(hasFacultyIds(filterGroups.getFacultyIds()));
            }
        }

        if (!CollectionUtils.isEmpty(filterGroups.getTeacherIds())) {
            if (specification == null) {
                specification = hasTeacherIds(filterGroups.getTeacherIds());
            } else{
                specification = specification.and(hasTeacherIds(filterGroups.getTeacherIds()));
            }
        }

        return specification;
    }

    private Specification<Group> hasYearIds(List<Long> yearIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> yearPredicates = new ArrayList<>();
            Path<Year> year = root.get("year");
            for (Long yearId : yearIds) {
                yearPredicates.add(criteriaBuilder.equal(year, yearId));
            }
            return criteriaBuilder.or(yearPredicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Group> hasFacultyIds(List<Long> facultyIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> facultyPredicates = new ArrayList<>();
            Path<Faculty> faculty = root.get("faculty");
            for (Long facultyId : facultyIds) {
                facultyPredicates.add(criteriaBuilder.equal(faculty, facultyId));
            }
            return criteriaBuilder.or(facultyPredicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Group> hasTeacherIds(List<Long> teacherIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> teacherPredicates = new ArrayList<>();
            Path<User> teacher = root.get("teacher");
            for (Long teacherId : teacherIds) {
                teacherPredicates.add(criteriaBuilder.equal(teacher, teacherId));
            }
            return criteriaBuilder.or(teacherPredicates.toArray(new Predicate[0]));
        };
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

    public int getQuantityCourseNumber(){
        return quantityCourseNumber;
    }

    public int getQuantityGroupNumber(){
        return quantityGroupNumber;
    }

    @Transactional
    public Group addNewGroup(Group newGroup) {
        if (newGroup.getTeacher() == null){
            newGroup.setTeacher(userService.getAdmin());
        }
        Group newGroup1 = groupRepo.save(newGroup);
        entityManager.flush();
        entityManager.refresh(newGroup1);
        return newGroup1;
    }
}
