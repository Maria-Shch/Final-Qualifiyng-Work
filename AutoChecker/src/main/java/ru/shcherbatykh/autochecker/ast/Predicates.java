package ru.shcherbatykh.autochecker.ast;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

import java.util.function.Predicate;

public final class Predicates {
    private Predicates() {
    }

    public static final Predicate<ResolvedType> TYPE_ANY_PREDICATE = type -> true;

    public static final Predicate<ResolvedType> NUMBER_TYPE_PREDICATE = type -> {
        ResolvedPrimitiveType resolvedPrimitiveType = null;
        if (type.isReferenceType() && type.asReferenceType().isUnboxable()) {
            ResolvedReferenceType resolvedReferenceType = type.asReferenceType();
            resolvedPrimitiveType = resolvedReferenceType.toUnboxedType().get();
        } else if (type.isPrimitive()) {
            resolvedPrimitiveType = type.asPrimitive();
        }

        return resolvedPrimitiveType != null && resolvedPrimitiveType.in(ResolvedPrimitiveType.BYTE,
                ResolvedPrimitiveType.SHORT, ResolvedPrimitiveType.INT, ResolvedPrimitiveType.LONG,
                ResolvedPrimitiveType.FLOAT, ResolvedPrimitiveType.DOUBLE);
    };

    public static final Predicate<ResolvedType> INT_NUMBER_TYPE_PREDICATE = type -> {
        ResolvedPrimitiveType resolvedPrimitiveType = null;
        if (type.isReferenceType() && type.asReferenceType().isUnboxable()) {
            ResolvedReferenceType resolvedReferenceType = type.asReferenceType();
            resolvedPrimitiveType = resolvedReferenceType.toUnboxedType().get();
        } else if (type.isPrimitive()) {
            resolvedPrimitiveType = type.asPrimitive();
        }

        return resolvedPrimitiveType != null && resolvedPrimitiveType.in(ResolvedPrimitiveType.BYTE,
                ResolvedPrimitiveType.SHORT, ResolvedPrimitiveType.INT, ResolvedPrimitiveType.LONG);
    };

    public static final Predicate<ResolvedType> REAL_NUMBER_TYPE_PREDICATE = type -> {
        ResolvedPrimitiveType resolvedPrimitiveType = null;
        if (type.isReferenceType() && type.asReferenceType().isUnboxable()) {
            ResolvedReferenceType resolvedReferenceType = type.asReferenceType();
            resolvedPrimitiveType = resolvedReferenceType.toUnboxedType().get();
        } else if (type.isPrimitive()) {
            resolvedPrimitiveType = type.asPrimitive();
        }

        return resolvedPrimitiveType != null && resolvedPrimitiveType.in(ResolvedPrimitiveType.FLOAT,
                ResolvedPrimitiveType.DOUBLE);
    };

    public static final Predicate<ResolvedType> INT_OR_LONG_TYPE_PREDICATE = type -> {
        ResolvedPrimitiveType resolvedPrimitiveType = null;
        if (type.isReferenceType() && type.asReferenceType().isUnboxable()) {
            ResolvedReferenceType resolvedReferenceType = type.asReferenceType();
            resolvedPrimitiveType = resolvedReferenceType.toUnboxedType().get();
        } else if (type.isPrimitive()) {
            resolvedPrimitiveType = type.asPrimitive();
        }

        return resolvedPrimitiveType != null
                && resolvedPrimitiveType.in(ResolvedPrimitiveType.INT, ResolvedPrimitiveType.LONG);
    };

    public static final Predicate<ResolvedType> STRING_TYPE_PREDICATE = type -> {
        if (type.isReferenceType()) {
            ResolvedReferenceType resolvedReferenceType = type.asReferenceType();
            return "java.lang.String".equals(resolvedReferenceType.getQualifiedName());
        }
        return false;
    };

    public static final Predicate<MethodDeclaration> MAIN_METHOD_PREDICATE = methodDeclaration ->
            methodDeclaration.isPublic() && methodDeclaration.isStatic()
                    && methodDeclaration.getType().isVoidType()
                    && "main".equals(methodDeclaration.getNameAsString())
                    && methodDeclaration.getParameters().size() == 1
                    && "String[]".equals(methodDeclaration.getParameter(0).getTypeAsString());
}
