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
import java.util.Arrays;
import java.util.List;

public class ASTBuilder {

    public static PixelLiteral px(int value) {
        return new PixelLiteral(value);
    }
    public static PercentageLiteral percent(int value) {
        return new PercentageLiteral(value);
    }
    public static ScalarLiteral scalar(int value) {
        return new ScalarLiteral(value);
    }
    public static ColorLiteral color(String hexCode) {
        return new ColorLiteral(hexCode);
    }
    public static BoolLiteral bool(boolean value) {
        return new BoolLiteral(value);
    }
    public static VariableReference varRef(String name) {
        return new VariableReference(name);
    }
    public static MultiplyOperation multiply(Expression a, Expression b){
        return new MultiplyOperation(a, b);
    }
    public static AddOperation addition(Expression a, Expression b) {
        return new AddOperation(a, b);
    }
    public static SubtractOperation subtract(Expression a, Expression b) {
        return new SubtractOperation(a, b);
    }
    public static TagSelector tag(String name) {return new TagSelector(name);}
    public static IdSelector id(String name) {return new IdSelector(name);}
    public static ClassSelector cls(String name) {return new ClassSelector(name);}

    public static AST styleSheet(ASTNode... nodes) {
        StyleSheet sheet = new StyleSheet();
        for (ASTNode node : nodes) {
            sheet.addChild(node);
        }
        return new AST(sheet);
    }

    public static StyleRule rule(String tagName, ASTNode... declarations) {
        StyleRule rule = new StyleRule();
        rule.addChild(new TagSelector(tagName));
        for (ASTNode decl : declarations) {
            rule.addChild(decl);
        }
        return rule;
    }

    public static StyleRule rule(Selector selector, Declaration ... declarations) {
        StyleRule rule = new StyleRule();
        rule.addChild(selector);
        for(Declaration d : declarations) {
            rule.addChild(d);
        }
        return rule;
    }

    public static Declaration decl(String property, Expression expression) {
        return new Declaration(property, expression);
    }

    public static VariableAssignment varAssignment(String name, Expression value) {
        return new VariableAssignment(name, value);
    }

    public static IfClause ifClause(ASTNode bool, ASTNode... bodyNodes) {
        ArrayList<ASTNode> body = new ArrayList<>(Arrays.asList(bodyNodes));
        return new IfClause((Expression) bool, body);
    }

    public static IfClause ifElseClause(
            ASTNode condition,
            ASTNode[] ifBodyNodes,
            ASTNode[] elseBodyNodes
    ) {

        if (!(condition instanceof BoolLiteral || condition instanceof VariableReference)) {
            throw new IllegalArgumentException("Condition must be a boolean or variable reference");
        }

        ArrayList<ASTNode> ifBody = new ArrayList<>(Arrays.asList(ifBodyNodes));
        ArrayList<ASTNode> elseBody = new ArrayList<>(Arrays.asList(elseBodyNodes));

        return new IfClause(
                (Expression) condition,
                ifBody,
                new ElseClause(elseBody)
        );
    }

    public static IfClause ifElseClause(
            ASTNode condition,
            ASTNode ifBody,
            ASTNode... elseBodyNodes
    ) {
        ASTNode[] ifArr = (ifBody == null) ? new ASTNode[0] : new ASTNode[]{ifBody};
        ASTNode[] elseArr = (elseBodyNodes == null) ? new ASTNode[0] : elseBodyNodes;

        return ifElseClause(
                condition,
                ifArr,
                elseArr
        );
    }

}
