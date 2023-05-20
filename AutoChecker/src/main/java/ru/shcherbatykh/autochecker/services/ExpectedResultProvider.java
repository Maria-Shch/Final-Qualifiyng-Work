package ru.shcherbatykh.autochecker.services;

import org.springframework.stereotype.Service;

@Service
public class ExpectedResultProvider {
    public String getExpectedResultForTaskLaunch(String taskId) {
        return switch (taskId) {
            case "1.1.2" ->
                    "0JrQu9C10L7Qv9Cw0YLRgNCwLCDRgNC+0YHRgjogMTUyCtCf0YPRiNC60LjQvSwg0YDQvtGB0YI6IDE2NwrQktC70LDQtNC40LzQuNGALCDRgNC+0YHRgjogMTg5";
            case "1.1.3" ->
                    "0JrQu9C10L7Qv9Cw0YLRgNCwCtCf0YPRiNC60LjQvSDQkNC70LXQutGB0LDQvdC00YAg0KHQtdGA0LPQtdC10LLQuNGHCtCc0LDRj9C60L7QstGB0LrQuNC5INCS0LvQsNC00LjQvNC40YA=";
            case "1.1.4" -> "MDowOjEwCjI6NDY6NDAKMzo0Njo0MA==";
            case "1.1.5" -> "0JTQvtC8INGBIDEg0Y3RgtCw0LbQvtC8CtCU0L7QvCDRgSA1INGN0YLQsNC20LDQvNC4CtCU0L7QvCDRgSAyMyDRjdGC0LDQttCw0LzQuA==";
            default -> null;
        };
    }
}
