package ru.shcherbatykh.codetester.tests;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.codetester.model.results.RunCodeResult;
import ru.shcherbatykh.codetester.rules.model.RuleViolation;
import ru.shcherbatykh.common.model.CodeTestResult;
import ru.shcherbatykh.common.model.CompilationDiagnostic;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface CodeTest {
    List<CodeTestResult> launchTest(CodeCheckContext codeCheckContext);

    default boolean isRunMainClassRequired() {
        return false;
    }

    default ObjectNode createDescriptionNode(String description) {
        if (StringUtils.isEmpty(description)) {
            return null;
        }
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("description", description);
        return node;
    }

    default ArrayNode createCompilationErrorsNode(List<CompilationDiagnostic> compilationErrors) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (CompilationDiagnostic compilationError : compilationErrors) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("lineNumber", compilationError.getLineNumber());
            node.put("position", compilationError.getPosition());
            if (StringUtils.isNotEmpty(compilationError.getMessage())) {
                node.put("message", compilationError.getMessage());
            }
            arrayNode.add(node);
        }
        return arrayNode;
    }

    default ObjectNode createRunCodeResultNode(RunCodeResult runCodeResult) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        if (StringUtils.isNotEmpty(runCodeResult.getExpectedValue()) || StringUtils.isNotEmpty(runCodeResult.getActualValue())) {
            node.put("expectedValue", runCodeResult.getExpectedValue());
        }
        if (StringUtils.isNotEmpty(runCodeResult.getActualValue()) || StringUtils.isNotEmpty(runCodeResult.getExpectedValue())) {
            node.put("actualValue", runCodeResult.getActualValue());
        }
        if (StringUtils.isNotEmpty(runCodeResult.getError())) {
            node.put("error", runCodeResult.getError());
        }
        return node;
    }

    default ArrayNode createRuleViolationsNode(List<RuleViolation> violations) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        Map<String, List<RuleViolation>> details = violations.stream()
                .filter(ruleViolation -> Objects.nonNull(ruleViolation.node()))
                .collect(Collectors.groupingBy(RuleViolation::node));
        List<RuleViolation> nonNodeViolations = violations.stream()
                .filter(ruleViolation -> Objects.isNull(ruleViolation.node()))
                .toList();
        if (!nonNodeViolations.isEmpty()) {
            details.put("—", nonNodeViolations);
        }
        for (Map.Entry<String, List<RuleViolation>> entry : details.entrySet()) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("node", entry.getKey());
            ArrayNode violationsArray = node.putArray("violations");
            for (RuleViolation violation : entry.getValue()) {
                ObjectNode violationNode = JsonNodeFactory.instance.objectNode();
                if (StringUtils.isNotEmpty(violation.rule())) {
                    violationNode.put("rule", violation.rule());
                }
                if (StringUtils.isNotEmpty(violation.description())) {
                    violationNode.put("description", violation.description());
                }
                violationsArray.add(violationNode);
            }
            arrayNode.add(node);
        }
        return arrayNode;
    }
}
