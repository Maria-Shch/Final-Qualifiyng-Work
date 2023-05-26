package ru.shcherbatykh.autochecker.rules.model;

public record RuleViolation(String rule, String node, String description, boolean reflexivity) {
    public RuleViolation(String rule, String node, String description) {
        this(rule, node, description, false);
    }

    public RuleViolation(String rule, String description) {
        this(rule, null, description, false);
    }
}
