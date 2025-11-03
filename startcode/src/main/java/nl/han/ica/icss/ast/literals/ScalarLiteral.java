package nl.han.ica.icss.ast.literals;

import nl.han.ica.icss.ast.Literal;

import java.util.Objects;

public class ScalarLiteral extends Literal {
    public int value;

    public ScalarLiteral(int value) {
        this.value = value;
    }

    public ScalarLiteral(String text) {
        this.value = Integer.parseInt(text);
    }

    @Override
    public String getNodeLabel() {
        return "Scalar literal (" + value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ScalarLiteral that = (ScalarLiteral) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Literal multiply(Literal rhs) {
        return switch (rhs) {
            case ScalarLiteral r -> new ScalarLiteral(this.value * r.value);
            case PixelLiteral r -> new PixelLiteral(this.value * r.value);
            case PercentageLiteral r -> new PercentageLiteral(this.value * r.value);
            default -> null;
        };
    }

    @Override
    public Literal add(Literal rhs) {
        return (rhs instanceof ScalarLiteral r)
                ? new ScalarLiteral(this.value + r.value)
                : null;
    }

    @Override
    public Literal subtract(Literal rhs) {
        return (rhs instanceof ScalarLiteral r)
                ? new ScalarLiteral(this.value - r.value)
                : null;
    }
}
