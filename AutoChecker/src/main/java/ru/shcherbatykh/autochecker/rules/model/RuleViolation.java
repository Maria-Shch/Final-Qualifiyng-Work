package ru.shcherbatykh.autochecker.rules.model;

public record RuleViolation(String rule, String node, String description) {
}
