package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.scoping.ScopeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static nl.han.ica.icss.scoping.ASTScopeRules.isScopingNode;

public class Evaluator implements Transform, ExpressionVisitor<Literal> {

    private ScopeManager<Literal> scopeManager;

    @Override
    public void apply(AST ast) {
        this.scopeManager = new ScopeManager<>();
        transform(ast.root);
    }

    private void transform(ASTNode node) {
        if (isScopingNode(node)) scopeManager.enterScope();

        switch (node) {
            case Declaration decl -> decl.expression = evaluate(decl.expression);
            case StyleRule rule -> rule.body = transformBody(rule.body);
            default -> {
            }
        }

        node.getChildren().forEach(this::transform);

        if (isScopingNode(node)) scopeManager.exitScope();
    }

    private ArrayList<ASTNode> transformBody(ArrayList<ASTNode> body) {
        ArrayList<ASTNode> newBody = new ArrayList<>();
        for (ASTNode child : body) {
            if (child instanceof IfClause ifc) {
                ArrayList<ASTNode> selected = getSelectedBody(ifc);
                newBody.addAll(transformBody(selected));
            } else {
                newBody.add(child);
            }
        }
        return newBody;
    }

    private ArrayList<ASTNode> getSelectedBody(IfClause ifc) {
        if (ifc.conditionalExpression instanceof BoolLiteral b && b.value) return ifc.body;
        else if (ifc.elseClause != null) return ifc.elseClause.body;
        return new ArrayList<>();
    }

    private Literal evaluate(@NotNull Expression expr) {
        return expr.accept(this);
    }

    @Override
    public Literal visitLiteral(Literal literal) {
        return literal;
    }

    @Override
    public Literal visitMultiplyOperation(MultiplyOperation mul) {
        return evaluate(mul.lhs).multiply(evaluate(mul.rhs));
    }

    @Override
    public Literal visitVariableReference(VariableReference ref) {
        return null;
    }

    @Override
    public Literal visitSubtractOperation(SubtractOperation sub) {
        return evaluate(sub.lhs).subtract(evaluate(sub.rhs));
    }

    @Override
    public Literal visitAddOperation(AddOperation add) {
        return evaluate(add.lhs).add(evaluate(add.rhs));
    }
}
