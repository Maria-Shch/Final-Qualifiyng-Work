package ru.shcherbatykh.application.utils;

import ru.shcherbatykh.application.models.Group;

import java.time.LocalDateTime;
import java.util.Objects;

public class CommonUtils {
    public static String getGroupName(Group group) {
        StringBuilder name = new StringBuilder();
        name.append(group.getLevelOfEdu().getLetter());

        if (!Objects.equals(group.getProfile().getName(), "Без профиля")){
            name.append(group.getProfile().getName());
        }

        name.append('-')
            .append(group.getFaculty().getName());

        if (!Objects.equals(group.getFormOfEdu().getLetter(), null)) {
            name.append(group.getFormOfEdu().getLetter());
        }

        name.append('-')
            .append(group.getCourseNumber())
            .append(group.getGroupNumber());

        return name.toString();
    }

    public static String getTimeToPrint(LocalDateTime creationTime) {
        int DD = creationTime.getDayOfMonth();
        int MM = creationTime.getMonthValue();
        int YY = creationTime.getYear();

        int HH = creationTime.getHour();
        int MN = creationTime.getMinute();
        return String.format("%02d:%02d %02d.%02d.%02d", HH, MN, DD, MM, YY);
    }
}
