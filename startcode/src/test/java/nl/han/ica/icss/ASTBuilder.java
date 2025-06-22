package nl.han.ica.icss;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class ASTBuilder {

    public static AST stylesheet(ASTNode... rules) {
        Stylesheet ss = new Stylesheet();
        for (ASTNode rule : rules) {
            ss.addChild(rule);
        }
        return new AST(ss);
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
        Declaration d = new Declaration(property);
        d.addChild(expression);
        return d;
    }

    public static VariableAssignment assign(String name, Expression value) {
        VariableAssignment a = new VariableAssignment();
        a.addChild(new VariableReference(name));
        a.addChild(value);
        return a;
    }

    public static Declaration declVar(String property, String varName) {
        Declaration d = new Declaration(property);
        d.addChild(new VariableReference(varName));
        return d;
    }
}
