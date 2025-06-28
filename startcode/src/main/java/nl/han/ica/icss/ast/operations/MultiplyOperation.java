package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;

public class MultiplyOperation extends Operation {

    public MultiplyOperation(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public MultiplyOperation() {
        super();
    }

    @Override
    public String getNodeLabel() {
        return "Multiply";
    }
}
