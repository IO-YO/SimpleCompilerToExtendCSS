package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.ExpressionVisitor;
import nl.han.ica.icss.ast.Operation;

public class SubtractOperation extends Operation {

    public SubtractOperation(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public SubtractOperation() {
        super();
    }

    @Override
    public String getNodeLabel() {
        return "Subtract";
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitSubtractOperation(this);
    }
}
