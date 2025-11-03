package nl.han.ica.icss.ast;

public abstract class Literal extends Expression {

    public abstract Literal multiply(Literal rhs);
    public abstract Literal add(Literal rhs);
    public abstract Literal subtract(Literal rhs);

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
