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
        for (int i = 0; i < body.size(); ) {
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
                if (!chosenBody.isEmpty()) {
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
    public Literal visitSubtractOperation(SubtractOperation sub) {
        Literal lhs = evaluate(sub.lhs);
        Literal rhs = evaluate(sub.rhs);

        return switch (lhs) {
            case PixelLiteral l when rhs instanceof PixelLiteral r -> new PixelLiteral(l.value - r.value);
            case PercentageLiteral l when rhs instanceof PercentageLiteral r -> new PercentageLiteral(l.value - r.value);
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
            case PercentageLiteral l when rhs instanceof PercentageLiteral r -> new PercentageLiteral(l.value + r.value);
            case ScalarLiteral l when rhs instanceof ScalarLiteral r -> new ScalarLiteral(l.value + r.value);
            default -> null;
        };
    }

    @Override
    public Literal visitVariableReference(VariableReference ref) {
        Expression lit = scopeManager.resolve(ref.name);
        return (Literal) lit;
    }
}
