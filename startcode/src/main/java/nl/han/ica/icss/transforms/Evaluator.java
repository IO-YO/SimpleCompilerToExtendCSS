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
            default -> {}
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
        Literal lhs = evaluate(mul.lhs);
        Literal rhs = evaluate(mul.rhs);

        return switch (lhs) {
            case PixelLiteral l when rhs instanceof ScalarLiteral r -> new PixelLiteral(l.value * r.value);
            case ScalarLiteral l when rhs instanceof PixelLiteral r -> new PixelLiteral(l.value * r.value);
            case PercentageLiteral l when rhs instanceof ScalarLiteral r -> new PercentageLiteral(l.value * r.value);
            case ScalarLiteral l when rhs instanceof PercentageLiteral r -> new PercentageLiteral(l.value * r.value);
            case ScalarLiteral l when rhs instanceof ScalarLiteral r -> new ScalarLiteral(l.value * r.value);
            default -> null;
        };
    }

    @Override
    public Literal visitVariableReference(VariableReference ref) {
        return null;
    }

    @Override
    public Literal visitSubtractOperation(SubtractOperation sub) {
        Literal lhs = evaluate(sub.lhs);
        Literal rhs = evaluate(sub.rhs);

        return switch (lhs) {
            case PixelLiteral l when rhs instanceof PixelLiteral r -> new PixelLiteral(l.value - r.value);
            case PercentageLiteral l when rhs instanceof PercentageLiteral r ->
                    new PercentageLiteral(l.value - r.value);
            case ScalarLiteral l when rhs instanceof ScalarLiteral r -> new ScalarLiteral(l.value - r.value);
            default -> null;
        };
    }

    @Override
    public Literal visitAddOperation(AddOperation op) {
        Literal lhs = evaluate(op.lhs);
        Literal rhs = evaluate(op.rhs);

        return switch (lhs) {
            case PixelLiteral l when rhs instanceof PixelLiteral r -> new PixelLiteral(l.value + r.value);
            case PercentageLiteral l when rhs instanceof PercentageLiteral r ->
                    new PercentageLiteral(l.value + r.value);
            case ScalarLiteral l when rhs instanceof ScalarLiteral r -> new ScalarLiteral(l.value + r.value);
            default -> null;
        };
    }
}
