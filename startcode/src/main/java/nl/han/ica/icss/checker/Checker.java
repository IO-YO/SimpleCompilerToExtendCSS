package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

import java.util.*;
import java.util.stream.Collectors;

public class Checker {

    private final Map<String, Set<Class<? extends Expression>>> allowedTypes = Map.of(
            "width", Set.of(PixelLiteral.class, PercentageLiteral.class),
            "height", Set.of(PixelLiteral.class, PercentageLiteral.class),
            "color", Set.of(ColorLiteral.class),
            "background-color", Set.of(ColorLiteral.class)
    );

    public void check(AST ast) {
        if (ast == null || ast.root == null) throw new IllegalArgumentException("AST or root cannot be null");

        IHANStack<ASTNode> stack = new HANStack();
        stack.push(ast.root);
        while (true){
            ASTNode currentNode;
            try {
                currentNode = stack.pop();
            } catch (EmptyStackException e) {
                break;
            }
            if (currentNode instanceof Declaration) {
                checkDeclaration((Declaration) currentNode);

            }

            ArrayList<ASTNode> children = currentNode.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
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
