package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EmptyStackException;

public class Checker {

    public void check(AST ast) {
        if (ast == null || ast.root == null) throw new IllegalArgumentException("AST or root cannot be null");

//        Deque<AST> nodeQueue = new ArrayDeque<>();
//        nodeQueue.add(ast.getRoot());
//        while (!nodeQueue.IsEmpty()) {
//            AST currentNode = nodeQueue.poll();
//            nodeQueue.addAll(currentNode.getChildren());
//        }
//
        IHANStack<ASTNode> stack = new HANStack();
        stack.push(ast.root);
        while (true){
            ASTNode currentNode;
            try {
                currentNode = (ASTNode) stack.pop();
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

    //TODO: Make dynamically
    private void checkDeclaration(Declaration currentNode) {
        if(currentNode.property.name.equals("width")) {
            if(!(currentNode.expression instanceof PixelLiteral)) {
                String currentExpr = currentNode.expression.getNodeLabel();
                currentNode.setError("Property 'width' can't be a '" + currentExpr + "'");
            }
        }
    }

}
