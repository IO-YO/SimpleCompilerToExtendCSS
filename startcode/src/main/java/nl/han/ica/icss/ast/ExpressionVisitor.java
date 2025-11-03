package nl.han.ica.icss.ast;

import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public interface ExpressionVisitor<T> {
    T visitLiteral(Literal literal);
    T visitAddOperation(AddOperation add);
    T visitSubtractOperation(SubtractOperation sub);
    T visitMultiplyOperation(MultiplyOperation mul);
    T visitVariableReference(VariableReference ref);
}
