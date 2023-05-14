package ru.shcherbatykh.autochecker.ast;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import org.apache.commons.lang3.StringUtils;

public final class AstUtils {
    private AstUtils() {
    }

    public static String getFullyQualifiedName(TypeDeclaration<?> typeDeclaration) {
        return typeDeclaration.getFullyQualifiedName().orElse(StringUtils.EMPTY);
    }

    public static boolean isExpectedReferenceType(ClassOrInterfaceType objectCreationType,
                                                  JavaParserFacade javaParserFacade,
                                                  TypeDeclaration<?> typeDeclaration) {
        ResolvedType resolvedType = javaParserFacade.convertToUsage(objectCreationType);
        ResolvedReferenceType resolvedReferenceType = resolvedType.asReferenceType();
        return getFullyQualifiedName(typeDeclaration).equals(resolvedReferenceType.getQualifiedName());
    }
}
