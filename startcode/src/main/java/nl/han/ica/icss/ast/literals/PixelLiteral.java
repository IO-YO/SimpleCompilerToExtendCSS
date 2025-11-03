package nl.han.ica.icss.ast.literals;

import nl.han.ica.icss.ast.Literal;

import java.util.Objects;

public class PixelLiteral extends Literal {
    public int value;

    public PixelLiteral(int value) {
        this.value = value;
    }

    public PixelLiteral(String text) {
        this.value = Integer.parseInt(text.substring(0, text.length() - 2));
    }

    @Override
    public String getNodeLabel() {
        return "Pixel literal (" + value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PixelLiteral that = (PixelLiteral) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Literal multiply(Literal rhs) {
        return (rhs instanceof ScalarLiteral r)
                ? new PixelLiteral(this.value * r.value)
                : null;
    }

    @Override
    public Literal add(Literal rhs) {
        return (rhs instanceof PixelLiteral r)
                ? new PixelLiteral(this.value + r.value)
                : null;
    }

    @Override
    public Literal subtract(Literal rhs) {
        return (rhs instanceof PixelLiteral r)
                ? new PixelLiteral(this.value - r.value)
                : null;
    }
}
