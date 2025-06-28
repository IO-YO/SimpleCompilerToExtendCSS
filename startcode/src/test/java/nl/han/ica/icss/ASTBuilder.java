package nl.han.ica.icss;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.selectors.TagSelector;

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

    public static VariableAssignment assign(String name, Expression value) {
        VariableAssignment ass = new VariableAssignment();
        ass.addChild(new VariableReference(name));
        ass.addChild(value);
        return ass;
    }

    public static Declaration declVar(String property, String varName) {
        Declaration decl = new Declaration(property);
        decl.addChild(new VariableReference(varName));
        return decl;
    }

    public static IfClause ifClause(ASTNode bool, ASTNode... bodyNodes) {
        IfClause clause = new IfClause();
        clause.addChild(bool);
        for (ASTNode node : bodyNodes) {
            clause.addChild(node);
        }
        return clause;
    }

    public static IfClause ifClauseWithElse(ASTNode condition, ASTNode ifBody, ASTNode... elseBodyNodes) {
        if (!(condition instanceof BoolLiteral || condition instanceof VariableReference))
            throw new IllegalArgumentException("Condition must be a boolean or variable reference");

        ElseClause elseClause = new ElseClause();
        for (ASTNode node : elseBodyNodes) {
            elseClause.addChild(node);
        }
        IfClause clause = new IfClause();
        clause.addChild(condition);
        if (ifBody != null) {
            clause.addChild(ifBody);
        }
        clause.addChild(elseClause);
        return clause;
    }

    public static AST ruleWithPropertyDeclaration(String ruleTag, String propertyName, Expression expression) {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule(ruleTag,
                        ASTBuilder.decl(propertyName, expression)
                )
        );
    }

}
