package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.scoping.ScopeManager;

import java.util.ArrayList;

import static nl.han.ica.icss.scoping.ASTScopeRules.isScopingNode;

public class Evaluator implements Transform {

    private ScopeManager<Literal> scopeManager;
    private ArrayList<ASTNode> nodesToRemove;

    @Override
    public void apply(AST ast) {
        this.scopeManager = new ScopeManager<>();
        this.nodesToRemove = new ArrayList<>();
        transform(ast.root);

        for(ASTNode child : nodesToRemove) {
            ast.root.removeChild(child);
        }
    }

    private void transform(ASTNode node) {
        if (isScopingNode(node)) scopeManager.enterScope();

        if (node instanceof Declaration decl)
            handleDeclaration(decl);
        if (node instanceof IfClause ifNode) transformIfClause(ifNode);

        node.getChildren().forEach(this::transform);

        if (isScopingNode(node)) scopeManager.exitScope();
    }

    private void transformIfClause(IfClause ifNode) {
        Expression expr = ifNode.conditionalExpression;
        if (expr instanceof BoolLiteral) {
            boolean condition = ((BoolLiteral) expr).value;
            if (!condition) nodesToRemove.add(ifNode);
        }

    }

    private void handleDeclaration(Declaration decl) {
        decl.expression = evaluate(decl.expression);
    }

    private Literal evaluate(Expression expr) {
        if (expr instanceof Literal lit) return lit;
        if (expr instanceof AddOperation op) return evaluateAdd(op);
        if (expr instanceof SubtractOperation sub) return evaluateSubtract(sub);
        if (expr instanceof MultiplyOperation mul) return evaluateMultiply(mul);
        return null;
    }

    private Literal evaluateMultiply(MultiplyOperation mul) {
        Literal lhs = evaluate(mul.lhs);
        Literal rhs = evaluate(mul.rhs);

        // px * scalar
        if (lhs instanceof PixelLiteral l && rhs instanceof ScalarLiteral r)
            return new PixelLiteral(l.value * r.value);
        // scalar * px
        if (lhs instanceof ScalarLiteral l && rhs instanceof PixelLiteral r)
            return new PixelLiteral(l.value * r.value);

        // percentage * scalar
        if (lhs instanceof PercentageLiteral l && rhs instanceof ScalarLiteral r)
            return new PercentageLiteral(l.value * r.value);
        // scalar * percentage
        if (lhs instanceof ScalarLiteral l && rhs instanceof PercentageLiteral r)
            return new PercentageLiteral(l.value * r.value);

        // scalar * scalar
        if (lhs instanceof ScalarLiteral l && rhs instanceof ScalarLiteral r)
            return new ScalarLiteral(l.value * r.value);

        return null;
    }

    private Literal evaluateSubtract(SubtractOperation sub) {
        Literal lhs = evaluate(sub.lhs);
        Literal rhs = evaluate(sub.rhs);
        if (lhs instanceof PixelLiteral l && rhs instanceof PixelLiteral r)
            return new PixelLiteral(l.value - r.value);
        if (lhs instanceof PercentageLiteral l && rhs instanceof PercentageLiteral r)
            return new PercentageLiteral(l.value - r.value);
        if (lhs instanceof ScalarLiteral l && rhs instanceof ScalarLiteral r)
            return new ScalarLiteral(l.value - r.value);
        return null;
    }

    private Literal evaluateAdd(AddOperation op) {
        Literal lhs = evaluate(op.lhs);
        Literal rhs = evaluate(op.rhs);

        if (lhs instanceof PixelLiteral l && rhs instanceof PixelLiteral r)
            return new PixelLiteral(l.value + r.value);
        if (lhs instanceof PercentageLiteral l && rhs instanceof PercentageLiteral r)
            return new PercentageLiteral(l.value + r.value);
        if (lhs instanceof ScalarLiteral l && rhs instanceof ScalarLiteral r)
            return new ScalarLiteral(l.value + r.value);

        return null;
    }

}
