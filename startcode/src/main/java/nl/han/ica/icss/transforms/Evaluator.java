package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.scoping.ScopeManager;

import java.util.ArrayList;
import java.util.List;

public class Evaluator implements Transform, ExpressionVisitor<Literal> {

    private ScopeManager<Expression> scopeManager;

    @Override
    public void apply(AST ast) {
        // TODO: Make the evaluator stateless by resolving this declaration of the ScopeManager
        this.scopeManager = new ScopeManager<>();
        transform(ast.root);
    }

    private void transform(StyleSheet sheet) {
        scopeManager.inNewScope(() -> transformBody(sheet.getChildren()));
    }

    private void transformBody(List<ASTNode> body) {
        for (int i = 0; i < body.size(); ) {
            ASTNode child = body.get(i);

            switch (child) {
                case VariableAssignment va -> {
                    scopeManager.declare(va.name.name, evaluate(va.expression));
                    body.remove(i);
                }
                case Declaration decl -> {
                    decl.expression = evaluate(decl.expression);
                    i++;
                }
                case StyleRule rule -> {
                    scopeManager.inNewScope(() -> transformBody(rule.body));
                    i++;
                }
                case IfClause ifc -> {
                    List<ASTNode> chosenBody = resolveIfCondition(ifc);

                    scopeManager.inNewScope(() -> transformBody(chosenBody));

                    body.remove(i);
                    if (!chosenBody.isEmpty()) {
                        body.addAll(i, chosenBody);
                        i += chosenBody.size();
                    }
                }
                default -> i++;
            }

        }
    }

    private List<ASTNode> resolveIfCondition(IfClause ifc) {
        BoolLiteral condition = (BoolLiteral) evaluate(ifc.conditionalExpression);
        if (condition.value) return ifc.body;
        else if (ifc.elseClause != null) return ifc.elseClause.body;
        else return new ArrayList<>();
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

    @Override
    public Literal visitVariableReference(VariableReference ref) {
        Expression lit = scopeManager.resolve(ref.name);
        return (Literal) lit;
    }
}
