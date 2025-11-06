package nl.han.ica.icss.checker;

import nl.han.ica.icss.scoping.ScopeManager;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.*;

public class Checker {

    ScopeManager<ExpressionType> scopeManager;

    private final Map<String, Set<ExpressionType>> allowedTypes = Map.of(
            "width", Set.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL),
            "height", Set.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE),
            "color", Set.of(ExpressionType.COLOR),
            "background-color", Set.of(ExpressionType.COLOR)
    );

    private final Map<Class<?>, ExpressionType>  typeMap = Map.of(
            PixelLiteral.class, ExpressionType.PIXEL,
            ColorLiteral.class, ExpressionType.COLOR,
            PercentageLiteral.class, ExpressionType.PERCENTAGE,
            ScalarLiteral.class, ExpressionType.SCALAR,
            BoolLiteral.class, ExpressionType.BOOL
    );

    public void check(AST ast) {
        if (ast == null || ast.root == null) {
            throw new IllegalArgumentException("AST or root cannot be null");
        }

        scopeManager = new ScopeManager<>();

        try (var _ = scopeManager.enter()) {
            checkBody(ast.root.getChildren());
        }
    }

    private void checkBody(List<ASTNode> body) {
        for (ASTNode child : body) {
            if(child instanceof VariableAssignment va) {
                handleVariableAssignment(va);
                continue;
            }

            if(child instanceof Declaration decl) {
                checkDeclaration(decl);
                continue;
            }

            if(child instanceof StyleRule rule) {
                try (var _ = scopeManager.enter()) {
                    checkBody(rule.getChildren());
                }
                continue;
            }

            if(child instanceof IfClause ifc) {
                checkIfCondition(ifc);

                try (var _ = scopeManager.enter()) {
                    checkBody(ifc.body);
                }

                if(ifc.elseClause != null) {
                    try (var _ = scopeManager.enter()) {
                        checkBody(ifc.elseClause.getChildren());
                    }
                }
            }

        }
    }

    private void checkIfCondition(IfClause ifc) {
        ExpressionType type = resolveExpressionType(ifc.conditionalExpression, ifc);
        if (type != ExpressionType.BOOL) {
            ifc.setError("If-condition must be a boolean, but got: " + type);
        }
    }

    private void handleVariableAssignment(VariableAssignment varAss) {
        ExpressionType type = resolveExpressionType(varAss.expression, varAss);
        String varName = varAss.name.name;
        if (scopeManager.existsInCurrentScope(varName)) {
            varAss.setError("Variable '" + varName + "' redeclared in the same scope");
        }
        scopeManager.declare(varName, type);
    }

    private ExpressionType resolveExpressionType(Expression expr, ASTNode errorTarget) {
        return switch (expr) {
            case VariableReference ref -> resolveVariableRef(ref, errorTarget);
            case Operation op -> resolveOperation(op, errorTarget);
            default -> typeMap.getOrDefault(expr.getClass(), ExpressionType.UNDEFINED);
        };
    }

    private ExpressionType resolveOperation(Operation op, ASTNode errorTarget) {
        return null;
    }

    private ExpressionType resolveVariableRef(VariableReference ref, ASTNode errorTarget) {
        ExpressionType type = scopeManager.resolve(ref.name);
        if (type == null) {
            errorTarget.setError("Unknown variable '" + ref.name + "'");
            return ExpressionType.UNDEFINED;
        }
        return type;
    }

    private void checkDeclaration(Declaration decl) {
        String propertyName = decl.property.name;
        Set<ExpressionType> allowed = allowedTypes.get(propertyName);

        if (allowed == null) {
            decl.setError("Unknown property '" + propertyName + "'");
            return;
        }

        if (decl.expression == null) {
            decl.setError("Property '" + propertyName + "' must have a value");
            return;
        }

        ExpressionType actualType = resolveExpressionType(decl.expression, decl);
        if (actualType == ExpressionType.UNDEFINED || allowed.contains(actualType)){
            return;
        }

        decl.setError("Invalid value type '" + actualType + "' for property '" + propertyName + "'");
    }

}
