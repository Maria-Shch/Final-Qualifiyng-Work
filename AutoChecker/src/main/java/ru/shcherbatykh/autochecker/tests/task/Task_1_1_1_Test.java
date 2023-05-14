package ru.shcherbatykh.autochecker.tests.task;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.rules.ClassVariableTypesRule;
import ru.shcherbatykh.autochecker.rules.ToStringMethodExistsRule;
import ru.shcherbatykh.autochecker.rules.model.RuleContext;
import ru.shcherbatykh.autochecker.rules.model.RuleViolation;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("task_1.1.1")
@Scope("prototype")
public class Task_1_1_1_Test extends AbstractOneClassTaskTest {
    private static final int EXPECTED_AMOUNT_OF_TYPES = 2;
    private static final int EXPECTED_AMOUNT_OF_NON_MAIN_CLASSES = 1;
    private static final int EXPECTED_AMOUNT_OF_CREATED_POINTS = 3;
    private static final Map<String, Object> CLASS_VARIABLE_TYPES_RULE_INPUT_CONTEXT = Map.of(
            ClassVariableTypesRule.EXPECTED_AMOUNT_OF_VARIABLES, 2,
            ClassVariableTypesRule.VARIABLE_2_TYPE_MAP, Map.of("ALL", "t_number")
    );

    private final ClassVariableTypesRule classVariableTypesRule;
    private final ToStringMethodExistsRule toStringMethodExistsRule;

    public Task_1_1_1_Test(ClassVariableTypesRule classVariableTypesRule,
                           ToStringMethodExistsRule toStringMethodExistsRule) {
        this.classVariableTypesRule = classVariableTypesRule;
        this.toStringMethodExistsRule = toStringMethodExistsRule;
    }

    @Override
    protected int getExpectedAmountOfTypes() {
        return EXPECTED_AMOUNT_OF_TYPES;
    }

    @Override
    protected int getExpectedAmountOfNonMainClasses() {
        return EXPECTED_AMOUNT_OF_NON_MAIN_CLASSES;
    }

    @Override
    protected int getExpectedAmountOfCreatedClasses() {
        return EXPECTED_AMOUNT_OF_CREATED_POINTS;
    }

    @Override
    protected List<RuleViolation> checkRules(CodeCheckContext codeCheckContext,
                                             ClassOrInterfaceDeclaration targetClass) {
        RuleContext ruleContext = new RuleContext();

        classVariableTypesRule.setContexts(ruleContext, CLASS_VARIABLE_TYPES_RULE_INPUT_CONTEXT);
        classVariableTypesRule.applyRule(targetClass, codeCheckContext);

        toStringMethodExistsRule.setContext(ruleContext);
        toStringMethodExistsRule.applyRule(targetClass, codeCheckContext);

        checkMainClass(codeCheckContext, targetClass, ruleContext);

        return ruleContext.getViolations();
    }
}
