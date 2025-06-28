package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;

public class AddOperation extends Operation {


    public AddOperation(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public AddOperation() {
        super();
    }

    @Override
    public String getNodeLabel() {
        return "Add";
    }
}
