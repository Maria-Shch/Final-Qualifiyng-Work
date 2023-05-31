package ru.shcherbatykh.Backend.classes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReasonOfProhibitionTesting {
    PREVIOUS_TASK_NOT_SOLVED("PREVIOUS_TASK_NOT_SOLVED"),
    TASK_ON_TEACHER_REVIEW("TASK_ON_TEACHER_REVIEW"),
    TASK_ON_TEACHER_CONSIDERATION("TASK_ON_TEACHER_CONSIDERATION"),
    TASK_ON_TESTING("TASK_ON_TESTING");

    private final String value;
}
