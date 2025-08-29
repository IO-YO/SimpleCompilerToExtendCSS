package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilderBase<T extends ASTBuilderBase<T>> {

    protected final List<ASTNode> body = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public T let(String name, Expression value) {
        VariableAssignment ass = new VariableAssignment(name, value);
        body.add(ass);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T decl(String property, Expression value) {
        Declaration declaration = new Declaration(property);
        declaration.addChild(value);
        body.add(declaration);
        return (T) this;
    }

    public static PixelLiteral px(int value) { return new PixelLiteral(value); }
    public static PercentageLiteral percent(int value) { return new PercentageLiteral(value); }
    public static ScalarLiteral scalar(int value) { return new ScalarLiteral(value); }
    public static ColorLiteral color(String hex) { return new ColorLiteral(hex); }
    public static BoolLiteral bool(boolean value) { return new BoolLiteral(value); }
    public static VariableReference var(String name) { return new VariableReference(name); }

    public static AddOperation add(Expression a, Expression b) { return new AddOperation(a, b); }
    public static SubtractOperation sub(Expression a, Expression b) { return new SubtractOperation(a, b); }
    public static MultiplyOperation mul(Expression a, Expression b) { return new MultiplyOperation(a, b); }

    public static TagSelector tag(String name) { return new TagSelector(name);}
    public static IdSelector id(String name) { return new IdSelector(name);}
    public static ClassSelector class_(String name) { return new ClassSelector(name);}
}
