package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.*;
import java.util.stream.Collectors;

import static nl.han.ica.icss.checker.ASTScopeRules.opensScope;

public class Checker {

    ScopeManager scopeManager;

    private final Map<String, Set<Class<? extends Expression>>> allowedTypes = Map.of(
            "width", Set.of(PixelLiteral.class, PercentageLiteral.class),
            "height", Set.of(PixelLiteral.class, PercentageLiteral.class),
            "color", Set.of(ColorLiteral.class),
            "background-color", Set.of(ColorLiteral.class)
    );

    public void check(AST ast) {
        if (ast == null || ast.root == null) throw new IllegalArgumentException("AST or root cannot be null");
        scopeManager = new ScopeManager();
        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (opensScope(node)) scopeManager.enterScope();

        if (node instanceof VariableAssignment) checkVariableAssignment((VariableAssignment) node);
        if (node instanceof VariableReference) checkVariableReference((VariableReference) node);
        if (node instanceof Declaration) checkDeclaration((Declaration) node);

        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }

        if(opensScope(node)) scopeManager.exitScope();
    }

    private void checkVariableReference(VariableReference node) {
    }

    private void checkVariableAssignment(ASTNode node) {

    }

    private void checkDeclaration(Declaration node) {
        Set<Class<? extends Expression>> types = allowedTypes.get(node.property.name);
        if (types == null) {
            node.setError("Unknown property '" + node.property.name + "'");
            return;
        }

        if (node.expression == null) {
            node.setError("Property '" + node.property.name + "' must have a value");
            return;
        }

        if (node.expression instanceof VariableReference varRef) {
            return;
        }

        boolean matches = types.stream()
                .anyMatch(clazz -> clazz.isInstance(node.expression));
        if (!matches) {
            node.setError("Property '" + node.property.name + "' must be one of: " +
                    types.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) +
                    ", but got: " + node.expression.getNodeLabel());
        }
    }
}
