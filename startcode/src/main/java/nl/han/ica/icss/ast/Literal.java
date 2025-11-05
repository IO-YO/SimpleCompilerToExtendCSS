package nl.han.ica.icss.ast;

public abstract class Literal extends Expression {

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
