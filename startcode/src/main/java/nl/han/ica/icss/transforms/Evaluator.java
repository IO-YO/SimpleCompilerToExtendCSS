package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.scoping.ScopeManager;

import java.util.ArrayList;

import static nl.han.ica.icss.scoping.ASTScopeRules.isScopingNode;

public class Evaluator implements Transform, ExpressionVisitor<Literal> {

    private ScopeManager<Expression> scopeManager;

    @Override
    public void apply(AST ast) {
        this.scopeManager = new ScopeManager<>();
        transform(ast.root);
    }

    private void transform(ASTNode node) {
        if (isScopingNode(node)) scopeManager.enterScope();

        switch (node) {
            case StyleSheet s -> {
                ArrayList<ASTNode> nodes = s.getChildren();
                for (ASTNode n : nodes) {
                    if(n instanceof VariableAssignment varAss){
                        scopeManager.declare(varAss.name.name, varAss.expression);
                        s.removeChild(n);
                    }
                }
            }
            case StyleRule rule -> {
                transformBody(node, rule.body);
            }
            case Declaration decl -> decl.expression = evaluate(decl.expression);
            default -> {
            }
        }

        node.getChildren().forEach(this::transform);

        if (isScopingNode(node)) scopeManager.exitScope();
    }

    private ArrayList<ASTNode> transformBody(ASTNode parent, ArrayList<ASTNode> body) {
        ArrayList<ASTNode> newBody = new ArrayList<>();
        for (ASTNode child : body) {
            if (child instanceof VariableAssignment varAss) {
                scopeManager.declare(varAss.name.name, varAss.expression);
                parent.removeChild(child);
            }
            if (child instanceof IfClause ifc) {
                ArrayList<ASTNode> selected = getSelectedBody(ifc);
                newBody.addAll(transformBody(child, selected));
                newBody.forEach(parent::addChild);
                parent.removeChild(child);
            } else {
                newBody.add(child);
            }
        }
        return newBody;
    }

    private ArrayList<ASTNode> getSelectedBody(IfClause ifc) {
        BoolLiteral result = (BoolLiteral) evaluate(ifc.conditionalExpression);

        if (result.value) {
            return ifc.body;
        }
        else if (ifc.elseClause != null) {
            return ifc.elseClause.body;
        }
        return new ArrayList<>();
    }

    private Literal evaluate(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public Literal visitLiteral(Literal literal) {
        return literal;
    }

    @Override
    public Literal visitAddOperation(AddOperation add) {
        return evaluate(add.lhs).add(evaluate(add.rhs));
    }

    @Override
    public Literal visitSubtractOperation(SubtractOperation sub) {
        return evaluate(sub.lhs).subtract(evaluate(sub.rhs));
    }

    @Override
    public Literal visitMultiplyOperation(MultiplyOperation mul) {
        return evaluate(mul.lhs).multiply(evaluate(mul.rhs));
    }

    @Override
    public Literal visitVariableReference(VariableReference ref) {
        Expression lit = scopeManager.resolve(ref.name);
        return (Literal) lit;
    }
}
