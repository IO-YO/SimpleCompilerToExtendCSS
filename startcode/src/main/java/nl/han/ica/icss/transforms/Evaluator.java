package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.scoping.ScopeManager;

import java.util.ArrayList;

public class Evaluator implements Transform, ExpressionVisitor<Literal> {

    private ScopeManager<Expression> scopeManager;

    @Override
    public void apply(AST ast) {
        this.scopeManager = new ScopeManager<>();
        transform(ast.root);
    }

    private void transform(StyleSheet sheet) {
        scopeManager.enterScope();
        transformBody(sheet.getChildren());
        scopeManager.exitScope();
    }

    private void transformBody(ArrayList<ASTNode> body) {
        for (int i = 0; i < body.size();) {
            ASTNode child = body.get(i);

            if (child instanceof VariableAssignment va) {
                Literal value = evaluate(va.expression);
                scopeManager.declare(va.name.name, value);
                body.remove(i);
                continue;
            }

            if (child instanceof Declaration decl) {
                decl.expression = evaluate(decl.expression);
                i++;
                continue;
            }

            if (child instanceof StyleRule rule) {
                scopeManager.enterScope();
                transformBody(rule.body);
                scopeManager.exitScope();
                i++;
                continue;
            }

            if (child instanceof IfClause ifc) {
                BoolLiteral condition = (BoolLiteral) evaluate(ifc.conditionalExpression);
                ArrayList<ASTNode> chosenBody = condition.value
                        ? ifc.body
                        : (ifc.elseClause != null ? ifc.elseClause.body : new ArrayList<>());

                scopeManager.enterScope();
                transformBody(chosenBody);
                scopeManager.exitScope();

                body.remove(i);
                if(!chosenBody.isEmpty()) {
                    body.addAll(i, chosenBody);
                    i += chosenBody.size();
                }
                continue;
            }
            i++;
        }
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
