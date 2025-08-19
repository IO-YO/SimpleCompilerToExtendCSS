package nl.han.ica.icss;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for constructing different types of Abstract Syntax Tree (AST) nodes,
 * such as stylesheets, style rules, declarations, and conditional clauses.
 */
public class ASTBuilder {

    public static AST stylesheet(ASTNode... rules) {
        Stylesheet sheet = new Stylesheet();
        for (ASTNode rule : rules) {
            sheet.addChild(rule);
        }
        return new AST(sheet);
    }

    public static Stylerule rule(String tagName, ASTNode... declarations) {
        Stylerule rule = new Stylerule();
        rule.addChild(new TagSelector(tagName));
        for (ASTNode decl : declarations) {
            rule.addChild(decl);
        }
        return rule;
    }

    public static Declaration decl(String property, Expression expression) {
        Declaration decl = new Declaration(property);
        decl.addChild(expression);
        return decl;
    }

    public static VariableAssignment assign(
            String name,
            Expression value
    ) {
        VariableAssignment ass = new VariableAssignment();
        ass.addChild(new VariableReference(name));
        ass.addChild(value);
        return ass;
    }

    public static Declaration declVar(
            String property,
            String varName
    ) {
        Declaration decl = new Declaration(property);
        decl.addChild(new VariableReference(varName));
        return decl;
    }

    public static IfClause ifClause(
            ASTNode bool,
            ASTNode... bodyNodes
    ) {
        ArrayList<ASTNode> body = new ArrayList<>(Arrays.asList(bodyNodes));
        return new IfClause((Expression) bool, body);
    }

    public static IfClause ifElseClause(
            ASTNode condition,
            ASTNode[] ifBodyNodes,
            ASTNode[] elseBodyNodes
    ) {

        if (!(condition instanceof BoolLiteral
                || condition instanceof VariableReference)) {
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

    public static AST ruleWithPropertyDeclaration(String ruleTag, String propertyName, Expression expression) {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule(ruleTag,
                        ASTBuilder.decl(propertyName, expression)
                )
        );
    }

}
