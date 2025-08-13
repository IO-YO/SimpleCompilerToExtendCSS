package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;

public class SubOperation extends Operation {

    public SubOperation() {
        super();
    }

    public SubOperation(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public String getNodeLabel() {
        return "Sub";
    }
}
