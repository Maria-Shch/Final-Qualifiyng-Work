package ru.shcherbatykh.codetester.rules.model;

import com.github.javaparser.ast.Node;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class RuleContext {
    private final Map<String, Map<Node, Object>> data;
    private final List<RuleViolation> violations;

    public RuleContext() {
        data = new HashMap<>();
        violations = new ArrayList<>();
    }

    public void saveInCtx(String key, Node nodeKey, Object value) {
        data.computeIfAbsent(key, v -> new HashMap<>()).put(nodeKey, value);
    }

    public void addViolation(RuleViolation ruleViolation) {
        violations.add(ruleViolation);
    }
}
