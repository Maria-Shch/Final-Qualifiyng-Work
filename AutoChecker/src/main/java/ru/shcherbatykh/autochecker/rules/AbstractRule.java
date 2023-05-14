package ru.shcherbatykh.autochecker.rules;

import com.github.javaparser.ast.Node;
import ru.shcherbatykh.autochecker.rules.model.RuleContext;
import ru.shcherbatykh.autochecker.rules.model.RuleViolation;

import java.util.Map;

public abstract class AbstractRule<T extends Node> implements Rule<T> {
    private RuleContext ruleContext;
    private Map<String, Object> inputContext;

    @Override
    public RuleContext getContext() {
        return ruleContext;
    }

    @Override
    public Map<String, Object> getInputContext() {
        return inputContext;
    }

    @Override
    public void setContext(RuleContext ruleContext) {
        this.ruleContext = ruleContext;
    }

    @Override
    public void setContexts(RuleContext ruleContext, Map<String, Object> inputContext) {
        this.ruleContext = ruleContext;
        this.inputContext = inputContext;
    }

    @Override
    public void saveInContext(String key, Node nodeKey, Object value) {
        ruleContext.saveInCtx(key, nodeKey, value);
    }

    protected void addViolation(String node, String description) {
        ruleContext.addViolation(new RuleViolation(getRuleName(), node, description));
    }

    protected abstract String getRuleName();
}
