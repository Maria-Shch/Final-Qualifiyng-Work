package ru.shcherbatykh.autochecker.services;

import org.springframework.stereotype.Service;

@Service
public class ExpectedResultProvider {
    public String getExpectedResultForTaskLaunch(String taskId) {
        return switch (taskId) {
            case "1.1.1" -> "ezU7OX0Kezc7LTh9CnswOzE5fQ==";
            case "1.1.2" ->
                    "0JrQu9C10L7Qv9Cw0YLRgNCwLCDRgNC+0YHRgjogMTUyCtCf0YPRiNC60LjQvSwg0YDQvtGB0YI6IDE2NwrQktC70LDQtNC40LzQuNGALCDRgNC+0YHRgjogMTg5";
            default -> null;
        };
    }
}
