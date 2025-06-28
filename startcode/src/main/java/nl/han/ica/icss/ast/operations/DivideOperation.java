package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;

public class DivideOperation extends Operation {

    public DivideOperation(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public DivideOperation() {
        super();
    }

    public String getNodeLabel() { return "Divide"; }
}
