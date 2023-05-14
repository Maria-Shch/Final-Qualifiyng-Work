package ru.shcherbatykh.autochecker.rules;

import com.github.javaparser.ast.Node;
import ru.shcherbatykh.autochecker.rules.model.RuleContext;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;

import java.util.Map;

public interface Rule<T extends Node> {
    RuleContext getContext();

    Map<String, Object> getInputContext();

    void setContext(RuleContext ruleContext);

    void setContexts(RuleContext ruleContext, Map<String, Object> inputContext);

    void saveInContext(String key, Node nodeKey, Object value);

    void applyRule(T node, CodeCheckContext codeCheckContext);
}
