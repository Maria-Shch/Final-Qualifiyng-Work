package ru.shcherbatykh.autochecker.ast;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
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

    public static boolean isNumberType(Type type, JavaParserFacade javaParserFacade) {
        ResolvedType resolvedType = javaParserFacade.convertToUsage(type);
        return Predicates.NUMBER_TYPE_PREDICATE.test(resolvedType);
    }

    public static boolean isIntegerNumberType(Type type, JavaParserFacade javaParserFacade) {
        ResolvedType resolvedType = javaParserFacade.convertToUsage(type);
        return Predicates.INT_NUMBER_TYPE_PREDICATE.test(resolvedType);
    }

    public static boolean isStringType(Type type, JavaParserFacade javaParserFacade) {
        ResolvedType resolvedType = javaParserFacade.convertToUsage(type);
        return Predicates.STRING_TYPE_PREDICATE.test(resolvedType);
    }
}
