package ru.shcherbatykh.Backend.utils;

import ru.shcherbatykh.Backend.models.Group;

import java.time.LocalDateTime;
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

    public static String getCreationTimeToPrint(LocalDateTime creationTime) {
        int DD = creationTime.getDayOfMonth();
        int MM = creationTime.getMonthValue();
        int YY = creationTime.getYear();

        int HH = creationTime.getHour();
        int MN = creationTime.getMinute();
        return String.format("%02d:%02d %02d.%02d.%02d", HH, MN, DD, MM, YY);
    }
}
