package ru.shcherbatykh.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.shcherbatykh.application.dto.ChangedGroupMembers;
import ru.shcherbatykh.application.dto.FilterGroups;
import ru.shcherbatykh.application.dto.GroupWithUsersStatInfo;
import ru.shcherbatykh.application.dto.UserStatInfo;
import ru.shcherbatykh.application.models.*;
import ru.shcherbatykh.application.repositories.GroupRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
public class GroupService {
    private final GroupRepo groupRepo;
    private final UserService userService;
    private final StudentTaskService studentTaskService;
    private final RequestService requestService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.group.quantity.course-number}")
    private int quantityCourseNumber;

    @Value("${app.group.quantity.group-number}")
    private int quantityGroupNumber;

    @Transactional
    public Optional<Group> findById(Long id){
        return groupRepo.findById(id);
    }

    public GroupService(GroupRepo groupRepo, UserService userService, StudentTaskService studentTaskService,
                        RequestService requestService) {
        this.groupRepo = groupRepo;
        this.userService = userService;
        this.studentTaskService = studentTaskService;
        this.requestService = requestService;
    }

    public List<Group> getGroups(){
        List<Group> groups = groupRepo.findAll();
        return groups.stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    public List<Group> getGroups(Specification<Group> specification){
        List<Group> groups = groupRepo.findAll(specification);
        return groups.stream()
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    public List<Group> getGroupsByTeacher(User teacher) {
        List<Group> groups = groupRepo.findAllByTeacher(teacher);
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

        if (!CollectionUtils.isEmpty(filterGroups.getTeacherIds())) {
            if (specification == null) {
                specification = hasTeacherIds(filterGroups.getTeacherIds());
            } else{
                specification = specification.and(hasTeacherIds(filterGroups.getTeacherIds()));
            }
        }

        if (!CollectionUtils.isEmpty(filterGroups.getLevelOfEduIds())) {
            if (specification == null) {
                specification = hasLevelOfEduIds(filterGroups.getLevelOfEduIds());
            } else{
                specification = specification.and(hasLevelOfEduIds(filterGroups.getLevelOfEduIds()));
            }
        }

        if (!CollectionUtils.isEmpty(filterGroups.getProfileIds())) {
            if (specification == null) {
                specification = hasProfileIds(filterGroups.getProfileIds());
            } else{
                specification = specification.and(hasProfileIds(filterGroups.getProfileIds()));
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

    private Specification<Group> hasLevelOfEduIds(List<Long> levelOfEduIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> levelOfEduPredicates = new ArrayList<>();
            Path<LevelOfEdu> levelOfEdu = root.get("levelOfEdu");
            for (Long levelOfEduId : levelOfEduIds) {
                levelOfEduPredicates.add(criteriaBuilder.equal(levelOfEdu, levelOfEduId));
            }
            return criteriaBuilder.or(levelOfEduPredicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Group> hasProfileIds(List<Long> profileIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> profilePredicates = new ArrayList<>();
            Path<Profile> profile = root.get("profile");
            for (Long profileId : profileIds) {
                profilePredicates.add(criteriaBuilder.equal(profile, profileId));
            }
            return criteriaBuilder.or(profilePredicates.toArray(new Predicate[0]));
        };
    }
    
    public List<GroupWithUsersStatInfo> convertGroupsToGroupsWithUsersStatInfo(List<Group> groups){
        List<GroupWithUsersStatInfo> groupWithUsersStatInfos = new ArrayList<>();
        for(Group group: groups){
            GroupWithUsersStatInfo groupWithUsersStatInfo = new GroupWithUsersStatInfo();
            groupWithUsersStatInfo.setGroup(group);
            List<UserStatInfo> userStatInfos = new ArrayList<>();
            List<User> users = userService.getSortedUsersOfGroupWithNullPassword(group);
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
        Group savedGroup = groupRepo.save(newGroup);
        entityManager.flush();
        entityManager.refresh(savedGroup);
        return savedGroup;
    }

    @Transactional
    public Group updateGroup(Group updatedGroup) {
        Group oldGroup = findById(updatedGroup.getId()).get();
        Long oldTeacherId = oldGroup.getTeacher().getId();
        if (!Objects.equals(oldTeacherId, updatedGroup.getTeacher().getId())){
            List<Long> studentIdsOfUpdatedGroup = userService.getSortedUsersOfGroup(oldGroup).stream()
                    .map(User::getId)
                    .toList();
            if (!CollectionUtils.isEmpty(studentIdsOfUpdatedGroup)){
                requestService.reassignRequests(oldTeacherId, updatedGroup.getTeacher().getId(), studentIdsOfUpdatedGroup);
            }
        }
        Group savedGroup = groupRepo.save(updatedGroup);
        entityManager.flush();
        entityManager.refresh(savedGroup);
        return savedGroup;
    }

    public boolean updateGroupMembers(Long groupId, ChangedGroupMembers changedGroupMembers) {
        userService.setGroup(changedGroupMembers.getUnselectedStudentsOfGroupIds(), null);
        requestService.reassignRequests(findById(groupId).get().getTeacher().getId(),
                userService.getAdmin().getId(),
                changedGroupMembers.getUnselectedStudentsOfGroupIds());
        userService.setGroup(changedGroupMembers.getSelectedStudentsWithoutGroupIds(), findById(groupId).get());
        requestService.reassignRequests(userService.getAdmin().getId(),
                findById(groupId).get().getTeacher().getId(),
                changedGroupMembers.getSelectedStudentsWithoutGroupIds());
        return true;
    }

    public boolean disbandGroup(long groupId) {
        List<Long> studentIds = userService.getSortedUsersOfGroup(findById(groupId).get()).stream()
                .map(User::getId)
                .toList();
        userService.setGroup(studentIds, null);
        return true;
    }

    public boolean deleteGroup(long groupId) {
        disbandGroup(groupId);
        groupRepo.delete(findById(groupId).get());
        return true;
    }

    public void revokeGroupsFromTeacher(long teacherId) {
        List<Group> teacherGroups = getGroupsByTeacher(userService.findById(teacherId).get());
        User admin = userService.getAdmin();
        for(Group group: teacherGroups){
            group.setTeacher(admin);
            groupRepo.save(group);
        }
    }
}
