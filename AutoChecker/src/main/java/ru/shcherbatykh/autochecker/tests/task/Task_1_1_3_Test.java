package ru.shcherbatykh.autochecker.tests.task;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.autochecker.ast.AstUtils;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.model.Constants;
import ru.shcherbatykh.autochecker.rules.ClassConstructorRule;
import ru.shcherbatykh.autochecker.rules.ClassVariableTypesRule;
import ru.shcherbatykh.autochecker.rules.ToStringMethodExistsRule;
import ru.shcherbatykh.autochecker.rules.model.RuleContext;
import ru.shcherbatykh.autochecker.rules.model.RuleViolation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component("task_1.1.3")
@Scope("prototype")
public class Task_1_1_3_Test extends AbstractOneClassTaskTest {
    private static final int EXPECTED_AMOUNT_OF_TYPES = 2;
    private static final int EXPECTED_AMOUNT_OF_NON_MAIN_CLASSES = 1;
    private static final int EXPECTED_AMOUNT_OF_CREATED_NAMES = 3;
    private static final Map<String, Object> CLASS_CONSTRUCTOR_RULE_INPUT_CONTEXT = Map.of(
            ClassConstructorRule.EXPECTED_AMOUNT_OF_CONSTRUCTORS, List.of(0, 1, 2),
            ClassConstructorRule.NON_DEFAULT_CONSTRUCTOR_PREDICATE, (BiFunction<CodeCheckContext, List<ConstructorDeclaration>, List<String>>)
                    (codeCheckContext, list) -> {
                        if (list.isEmpty()) {
                            return Collections.emptyList();
                        } else if (list.size() == 1) {
                            ConstructorDeclaration constructorDeclaration = list.iterator().next();
                            NodeList<Parameter> parameters = constructorDeclaration.getParameters();
                            if (parameters.size() != 3
                                    || !AstUtils.isStringType(parameters.get(0).getType(), codeCheckContext.getJavaParserFacade())
                                    || !AstUtils.isStringType(parameters.get(1).getType(), codeCheckContext.getJavaParserFacade())
                                    || !AstUtils.isStringType(parameters.get(2).getType(), codeCheckContext.getJavaParserFacade())) {
                                return List.of(MessageFormat.format("Неверный конструктор с параметрами. " +
                                                "Ожидалось: 3 строковых параметра. " +
                                                "Найдено: {0} параметра с типами данных - {1}.",
                                        parameters.size(),
                                        parameters.stream()
                                                .map(Parameter::getTypeAsString)
                                                .collect(Collectors.joining(", "))
                                ));
                            } else {
                                return Collections.emptyList();
                            }
                        } else {
                            return List.of("Неверное количество конструкторов с параметрами. " +
                                    "Ожидалось: 0 или 1. Найдено: " + list.size());
                        }
                    }
    );
    private static final Map<String, Object> CLASS_VARIABLE_TYPES_RULE_INPUT_CONTEXT = Map.of(
            ClassVariableTypesRule.EXPECTED_AMOUNT_OF_VARIABLES, 3,
            ClassVariableTypesRule.VARIABLE_2_TYPE_MAP, Map.of(ClassVariableTypesRule.ALL_VAR, "java.lang.String")
    );

    private final ClassConstructorRule classConstructorRule;
    private final ClassVariableTypesRule classVariableTypesRule;
    private final ToStringMethodExistsRule toStringMethodExistsRule;

    @Autowired
    public Task_1_1_3_Test(ClassConstructorRule classConstructorRule,
                           ClassVariableTypesRule classVariableTypesRule,
                           ToStringMethodExistsRule toStringMethodExistsRule) {
        this.classConstructorRule = classConstructorRule;
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
        return EXPECTED_AMOUNT_OF_CREATED_NAMES;
    }

    @Override
    protected List<RuleViolation> checkRules(CodeCheckContext codeCheckContext,
                                             ClassOrInterfaceDeclaration targetClass) {
        RuleContext ruleContext = new RuleContext();

        classConstructorRule.setContexts(ruleContext, CLASS_CONSTRUCTOR_RULE_INPUT_CONTEXT);
        classConstructorRule.applyRule(targetClass, codeCheckContext);

        classVariableTypesRule.setContexts(ruleContext, CLASS_VARIABLE_TYPES_RULE_INPUT_CONTEXT);
        classVariableTypesRule.applyRule(targetClass, codeCheckContext);

        toStringMethodExistsRule.setContext(ruleContext);
        toStringMethodExistsRule.applyRule(targetClass, codeCheckContext);

        checkMainClass(codeCheckContext, targetClass, ruleContext);

        if (!ruleContext.getData().containsKey(Constants.SKIP_REFLEXIVITY_CREATION)
                || !ruleContext.getData().get(Constants.SKIP_REFLEXIVITY_CREATION).containsKey(targetClass)
                || !((boolean) ruleContext.getData().get(Constants.SKIP_REFLEXIVITY_CREATION).get(targetClass))) {
            checkReflectively(targetClass, codeCheckContext.findNonMainClassByDeclaration(targetClass), ruleContext);
        }

        return ruleContext.getViolations();
    }

    private void checkReflectively(ClassOrInterfaceDeclaration targetClass, Class<?> clazz, RuleContext ruleContext) {
        Field[] declaredFields = clazz.getDeclaredFields();
        declaredFields[0].setAccessible(true);
        declaredFields[1].setAccessible(true);
        declaredFields[2].setAccessible(true);

        Object clazzInstance = null;
        boolean defaultConstructorExists = (boolean) ruleContext.getData().get("default_constructor").get(targetClass);
        if (defaultConstructorExists) {
            try {
                clazzInstance = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                log.error("Error during class object creation using default constructor", e);
                RuleViolation violation = new RuleViolation("Проверка результата метода toString",
                        AstUtils.getFullyQualifiedName(targetClass),
                        "Не удалось создать экземпляр класса с помощью конструктора по умолчанию в связи с ошибкой: "
                                + e.getMessage(),
                        true
                );
                ruleContext.addViolation(violation);
            }
        }

        tryInvokeToStringMethod(targetClass, clazz, declaredFields, clazzInstance, ruleContext, "Клеопатра", null, null);
        tryInvokeToStringMethod(targetClass, clazz, declaredFields, clazzInstance, ruleContext, "Александр", "Сергеевич", "Пушкин");
        tryInvokeToStringMethod(targetClass, clazz, declaredFields, clazzInstance, ruleContext, "Владимир", null, "Маяковский");
        tryInvokeToStringMethod(targetClass, clazz, declaredFields, clazzInstance, ruleContext, "", null, "Бонапарт");
    }

    private void tryInvokeToStringMethod(ClassOrInterfaceDeclaration targetClass, Class<?> clazz, Field[] fields,
                                         Object clazzInstance, RuleContext ruleContext,
                                         String name, String patronymicName, String surname) {
        List<String> expectedResults = getExpectedResults(name, patronymicName, surname);

        if (clazzInstance == null) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                clazzInstance = constructor.newInstance(surname, name, patronymicName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                log.error("Error during class object creation", e);
                String expectedResultStr = String.join(" или ", expectedResults);
                addViolationForToStringMethod(targetClass, ruleContext, expectedResultStr, e.getMessage());
                return;
            }
        } else {
            try {
                fields[0].set(clazzInstance, surname);
                fields[1].set(clazzInstance, name);
                fields[2].set(clazzInstance, patronymicName);
            } catch (IllegalAccessException e) {
                log.error("Error during field value population", e);
                String expectedResultStr = String.join(" или ", expectedResults);
                addViolationForToStringMethod(targetClass, ruleContext, expectedResultStr, e.getMessage());
            }
        }

        String actualString = clazzInstance.toString();
        if (!expectedResults.contains(actualString)) {
            String expectedResultStr = String.join(" или ", expectedResults);
            addViolationForToStringMethod(targetClass, ruleContext, expectedResultStr, actualString);
        }
    }

    private List<String> getExpectedResults(String first, String second, String third) {
        return Stream.of(
                createER(first, second, third),
                createER(first, third, second),
                createER(second, first, third),
                createER(second, third, first),
                createER(third, first, second),
                createER(third, second, first)
        ).distinct().toList();
    }

    private String createER(String first, String second, String third) {
        String res = "";
        if (StringUtils.isNotEmpty(third)) {
            res += third;
        }
        if (StringUtils.isNotEmpty(first)) {
            if (!res.isEmpty()) {
                res += " ";
            }
            res += first;
        }
        if (StringUtils.isNotEmpty(second)) {
            if (!res.isEmpty()) {
                res += " ";
            }
            res += second;
        }
        return res;
    }

    private void addViolationForToStringMethod(ClassOrInterfaceDeclaration targetClass,
                                               RuleContext ruleContext,
                                               String expectedResult,
                                               String actualResult) {
        RuleViolation violation = new RuleViolation("Проверка результата метода toString",
                AstUtils.getFullyQualifiedName(targetClass),
                MessageFormat.format("Неверный результат работы метода toString. Ожидалось: {0}. Найдено: {1}.",
                        expectedResult, actualResult)
        );
        ruleContext.addViolation(violation);
    }
}