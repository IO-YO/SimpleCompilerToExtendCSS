package nl.han.ica.icss.checker;

import nl.han.ica.icss.scoping.ScopeGuard;
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
        if (ast == null
                || ast.root == null) throw new IllegalArgumentException("AST or root cannot be null");

        scopeManager = new ScopeManager<>();

        scopeManager.enterScope();
        checkBody(ast.root.getChildren());
        scopeManager.exitScope();
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
                try (var ignore = new ScopeGuard(scopeManager)) {
                    checkBody(rule.getChildren());
                }
                continue;
            }

            if(child instanceof IfClause ifc) {
                checkIfCondition(ifc);

                try (var ignore = new ScopeGuard(scopeManager)) {
                    checkBody(ifc.body);
                }

                if(ifc.elseClause != null) {
                    try (var ignore = new ScopeGuard(scopeManager)) {
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
        if (expr instanceof VariableReference varRef) {
            ExpressionType type = scopeManager.resolve(varRef.name);
            if (type == null) {
                errorTarget.setError("Unknown variable '" + varRef.name + "'");
                return ExpressionType.UNDEFINED;
            }
            return type;
        }
        return typeMap.getOrDefault(expr.getClass(), ExpressionType.UNDEFINED);
    }

    private void checkDeclaration(Declaration node) {
        String propertyName = node.property.name;
        Set<ExpressionType> allowed = allowedTypes.get(propertyName);

        if (allowed == null) {
            node.setError("Unknown property '" + propertyName + "'");
            return;
        }

        if (node.expression == null) {
            node.setError("Property '" + propertyName + "' must have a value");
            return;
        }

        ExpressionType actualType = resolveExpressionType(node.expression, node);
        if (actualType == ExpressionType.UNDEFINED
                || allowed.contains(actualType))
            return;

        node.setError("Invalid value type '" + actualType + "' for property '" + propertyName + "'");
    }
}
