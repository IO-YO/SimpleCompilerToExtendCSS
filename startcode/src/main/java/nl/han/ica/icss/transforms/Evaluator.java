package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.scoping.ScopeManager;

import static nl.han.ica.icss.scoping.ASTScopeRules.isScopingNode;

public class Evaluator implements Transform {

    private ScopeManager<Literal> scopeManager;

    @Override
    public void apply(AST ast) {
        this.scopeManager = new ScopeManager<>();
        transform(ast.root);
    }

    private void transform(ASTNode node) {
        if (isScopingNode(node)) scopeManager.enterScope();

        if (node instanceof Declaration decl) handleDeclaration(decl);

        node.getChildren().forEach(this::transform);

        if (isScopingNode(node)) scopeManager.exitScope();
    }

    private void handleDeclaration(Declaration decl) {
        decl.expression = evaluate(decl.expression);
    }

    private Literal evaluate(Expression expr) {
        if (expr instanceof Literal lit) return lit;
        if (expr instanceof AddOperation op) return evaluateAdd(op);
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
