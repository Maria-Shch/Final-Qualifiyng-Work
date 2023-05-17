package ru.shcherbatykh.autochecker.rules;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.autochecker.ast.AstUtils;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.model.Constants;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ClassConstructorRule extends AbstractRule<ClassOrInterfaceDeclaration> {
    public static final String EXPECTED_AMOUNT_OF_CONSTRUCTORS = "EXPECTED_AMOUNT_OF_CONSTRUCTORS";
    public static final String PROPER_DEFAULT_CONSTRUCTOR_EXISTS = "DEFAULT_CONSTRUCTOR_EXISTS";
    public static final String NON_DEFAULT_CONSTRUCTOR_PREDICATE = "NON_DEFAULT_CONSTRUCTOR_PREDICATE";

    @Override
    protected String getRuleName() {
        return "Проверка конструкторов класса";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void applyRule(ClassOrInterfaceDeclaration node, CodeCheckContext codeCheckContext) {
        List<ConstructorDeclaration> constructorDeclarations = node.findAll(ConstructorDeclaration.class);
        List<Integer> expectedAmountOfConstructors = (List<Integer>) getInputContext().get(EXPECTED_AMOUNT_OF_CONSTRUCTORS);
        if (!expectedAmountOfConstructors.contains(constructorDeclarations.size())) {
            addViolation(AstUtils.getFullyQualifiedName(node), MessageFormat.format(
                    "Неверное количество конструкторов в классе. Ожидалось: {0}. Найдено: {1}.",
                    expectedAmountOfConstructors.stream().map(Object::toString).collect(Collectors.joining(" или ")),
                    constructorDeclarations.size()));
            saveInContext(Constants.SKIP_REFLEXIVITY_CREATION, node, true);
            return;
        }

        boolean skipReflexivityCreation = false;
        boolean defaultConstructorExists;
        List<ConstructorDeclaration> nonDefaultConstructors;
        if (constructorDeclarations.isEmpty()) {
            defaultConstructorExists = true;
            nonDefaultConstructors = Collections.emptyList();
        } else {
            defaultConstructorExists = constructorDeclarations.stream()
                    .anyMatch(constructorDeclaration -> constructorDeclaration.getParameters().isEmpty()
                            && !constructorDeclaration.isPrivate());
            nonDefaultConstructors = constructorDeclarations.stream()
                    .filter(constructorDeclaration -> !constructorDeclaration.getParameters().isEmpty())
                    .toList();
        }

        if (getInputContext().containsKey(PROPER_DEFAULT_CONSTRUCTOR_EXISTS)) {
            boolean expectedDefaultConstructorExists = (boolean) getInputContext().get(PROPER_DEFAULT_CONSTRUCTOR_EXISTS);
            if (expectedDefaultConstructorExists && !defaultConstructorExists) {
                addViolation(AstUtils.getFullyQualifiedName(node), "В классе должен быть конструктор по умолчанию.");
                skipReflexivityCreation = true;
            } else if (!expectedDefaultConstructorExists && defaultConstructorExists) {
                addViolation(AstUtils.getFullyQualifiedName(node), "В классе не должно быть конструктора по умолчанию.");
                skipReflexivityCreation = true;
            }
        }
        saveInContext("default_constructor", node, defaultConstructorExists);

        BiFunction<CodeCheckContext, List<ConstructorDeclaration>, List<String>> nonDefaultConstructorPredicate =
                (BiFunction<CodeCheckContext, List<ConstructorDeclaration>, List<String>>) getInputContext().get(NON_DEFAULT_CONSTRUCTOR_PREDICATE);
        List<String> violations = nonDefaultConstructorPredicate.apply(codeCheckContext, nonDefaultConstructors);
        violations.forEach(violation -> addViolation(AstUtils.getFullyQualifiedName(node), violation));
        skipReflexivityCreation = skipReflexivityCreation || !violations.isEmpty();

        saveInContext(Constants.SKIP_REFLEXIVITY_CREATION, node, skipReflexivityCreation);
    }
}
