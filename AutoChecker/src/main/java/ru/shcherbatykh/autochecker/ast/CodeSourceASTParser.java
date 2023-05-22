package ru.shcherbatykh.autochecker.ast;

import com.github.javaparser.ParseException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.classes.CodeCheckRequest;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.model.CodeSource;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class CodeSourceASTParser {
    public CodeCheckContext parseCodeSources(CodeCheckRequest codeCheckRequest) throws ParseException {
        List<String> plainCodeList = new ArrayList<>();
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        for (CodeSource codeSource : codeCheckRequest.getCodeSources()) {
            String code = new String(Base64.getDecoder().decode(codeSource.getBody()));
            plainCodeList.add(code);
            CompilationUnit compilationUnit = StaticJavaParser.parse(code);
            compilationUnits.add(compilationUnit);
        }

        List<Triple<CompilationUnit, ClassOrInterfaceDeclaration, MethodDeclaration>> mainUnits = findMainClasses(compilationUnits);
        log.debug("mainUnits: {}", mainUnits);
        if (mainUnits.size() > 1) {
            throw new ParseException("Найдено более одного класса с main методом");
        }

        return CodeCheckContext.builder()
                .studentId(codeCheckRequest.getStudentId())
                .taskPath(codeCheckRequest.getTaskPath())
                .requestUuid(codeCheckRequest.getRequestUuid())
                .plainCodeSources(plainCodeList)
                .compilationUnits(compilationUnits)
                .mainUnit(mainUnits.isEmpty() ? null : mainUnits.iterator().next().getLeft())
                .mainClass(mainUnits.isEmpty() ? null : mainUnits.iterator().next().getMiddle())
                .mainMethod(mainUnits.isEmpty() ? null : mainUnits.iterator().next().getRight())
                .build();
    }

    private List<Triple<CompilationUnit, ClassOrInterfaceDeclaration, MethodDeclaration>> findMainClasses(
            List<CompilationUnit> compilationUnits) throws ParseException {
        List<Triple<CompilationUnit, ClassOrInterfaceDeclaration, MethodDeclaration>> mainClasses = new ArrayList<>();
        for (CompilationUnit compilationUnit : compilationUnits) {
            List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class,
                    ClassOrInterfaceDeclaration::isClassOrInterfaceDeclaration);
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classes) {
                List<MethodDeclaration> mainMethods = classOrInterfaceDeclaration
                        .findAll(MethodDeclaration.class, Predicates.MAIN_METHOD_PREDICATE);
                if (mainMethods.size() > 1) {
                    throw new ParseException("Найдено более одного main метода в классе "
                            + AstUtils.getFullyQualifiedName(classOrInterfaceDeclaration));
                }
                if (!mainMethods.isEmpty()) {
                    mainClasses.add(Triple.of(compilationUnit, classOrInterfaceDeclaration, mainMethods.iterator().next()));
                }
            }

        }
        return mainClasses;
    }
}
