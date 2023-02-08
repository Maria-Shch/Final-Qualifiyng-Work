package ru.shcherbatykh.Backend.utils;

import ru.shcherbatykh.Backend.models.Group;

import java.util.Objects;

public class CommonUtils {
    public static String getGroupName(Group group) {
        StringBuilder name = new StringBuilder();
        if(Objects.equals(group.getLevelOfEdu().getName(), "Бакалавриат")) name.append("б");
        if(Objects.equals(group.getLevelOfEdu().getName(), "Магистратура")) name.append("м");

        if (Objects.equals(group.getProfile().getName(), "Без профиля")) name.append("-");
        else name.append(group.getProfile().getName());

        if (Objects.equals(group.getFormOfEdu().getName(), "Заочная")) name.append('з');

        name.append('-')
            .append(group.getFaculty().getName())
            .append('-')
            .append(group.getCourseNumber())
            .append(group.getGroupNumber());

        return name.toString();
    }
}
