package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.*;

import static nl.han.ica.icss.checker.ASTScopeRules.isScopingNode;

public class Checker {

    ScopeManager scopeManager;

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
        if (ast == null || ast.root == null) throw new IllegalArgumentException("AST or root cannot be null");
        scopeManager = new ScopeManager();
        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (isScopingNode(node)) scopeManager.enterScope();

        if (node instanceof VariableAssignment) handleVariableAssignment((VariableAssignment) node);
        if (node instanceof Declaration) checkDeclaration((Declaration) node);

        node.getChildren().forEach(this::checkNode);

        if(isScopingNode(node)) scopeManager.exitScope();
    }

    private ExpressionType resolveVariableReference(VariableReference node) {
        ExpressionType refType = scopeManager.resolve(node.name);
        if (refType == null) {
            node.setError("Unknown variable '" + node.name + "'");
            return ExpressionType.UNDEFINED;
        } else return refType;
    }

    private void handleVariableAssignment(VariableAssignment node) {
        String name = node.name.name;
        ExpressionType type;
        if (node.expression instanceof VariableReference varRef) {
            type = resolveVariableReference(varRef);
        } else type = getExpressionType(node.expression);
        scopeManager.declare(name, type);
    }

    private ExpressionType getExpressionType(Expression node) {
        return typeMap.getOrDefault(node.getClass(), ExpressionType.UNDEFINED);
    }

    private void checkDeclaration(Declaration node) {
        Set<ExpressionType> allowed = allowedTypes.get(node.property.name);

        if (allowed == null) {
            node.setError("Unknown property '" + node.property.name + "'");
            return;
        }

        if (node.expression == null) {
            node.setError("Property '" + node.property.name + "' must have a value");
            return;
        }

        ExpressionType actualType;

        if (node.expression instanceof VariableReference) {
            actualType = resolveVariableReference((VariableReference) node.expression);
            if (actualType == ExpressionType.UNDEFINED) return;
        } else actualType = getExpressionType(node.expression);

        if (!allowed.contains(actualType)) {
            node.setError("Invalid value type '" + actualType + "' for property '" + node.property.name + "'");
        }
    }
}
