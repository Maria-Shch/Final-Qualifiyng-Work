package ru.shcherbatykh.codetester.rules;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.codetester.ast.AstUtils;
import ru.shcherbatykh.codetester.model.CodeCheckContext;

import java.text.MessageFormat;
import java.util.List;

@Component
@Scope("prototype")
public class ToStringMethodExistsRule extends AbstractRule<ClassOrInterfaceDeclaration> {
    @Override
    public void applyRule(ClassOrInterfaceDeclaration node, CodeCheckContext codeCheckContext) {
        List<MethodDeclaration> toStringMethods = node.findAll(MethodDeclaration.class,
                methodDeclaration -> methodDeclaration.isPublic() && !methodDeclaration.isStatic()
                        && "toString".equals(methodDeclaration.getNameAsString())
                        && "String".equals(methodDeclaration.getTypeAsString())
                        && methodDeclaration.getParameters().size() == 0);
        if (toStringMethods.size() != 1) {
            addViolation(AstUtils.getFullyQualifiedName(node), getDescription(toStringMethods.size()));
            return;
        }
        saveInContext("toString", node, toStringMethods.iterator().next());
    }

    private String getDescription(int actualCountOfMethods) {
        return MessageFormat.format("Неверное количество методов toString. Ожидалось: {0}. Найдено: {1}.",
                1, actualCountOfMethods);
    }

    @Override
    protected String getRuleName() {
        return "Проверка существования метода toString";
    }
}
