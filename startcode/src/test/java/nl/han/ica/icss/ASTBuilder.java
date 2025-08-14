package nl.han.ica.icss;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.selectors.TagSelector;

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

    /**
     * Creates an {@code IfClause} object by adding a conditional expression
     * and a series of body nodes representing the execution logic when the
     * condition evaluates to true.
     *
     * @param bool the conditional {@code ASTNode} that determines whether
     *             the body nodes will be executed. Typically an {@code Expression}.
     * @param bodyNodes an array of {@code ASTNode} objects representing the body
     *                  to be executed when the condition is true.
     * @return an {@code IfClause} object containing the conditional expression
     *         and the specified body nodes.
     */
    public static IfClause ifClause(ASTNode bool, ASTNode... bodyNodes) {
        IfClause clause = new IfClause();
        clause.addChild(bool);
        for (ASTNode node : bodyNodes) {
            clause.addChild(node);
        }
        return clause;
    }

    /**
     * Creates an {@code IfClause} object with a condition, a list of nodes to execute if the condition is true,
     * and a list of nodes to execute if the condition is false.
     *
     * @param condition the conditional expression for the {@code IfClause}. This must be a boolean literal
     *                  or a variable reference; otherwise, an {@code IllegalArgumentException} will be thrown.
     * @param ifBodyNodes an array of {@code ASTNode} objects representing the body to execute when the condition is true.
     *                    Can be {@code null}, in which case no nodes are added for the "if" branch.
     * @param elseBodyNodes an array of {@code ASTNode} objects representing the body to execute when the condition is false.
     *                      Can be {@code null}, in which case no nodes are added for the "else" branch.
     * @return an {@code IfClause} object containing the condition, "if" body nodes, and optional "else" body nodes.
     * @throws IllegalArgumentException if the condition is not a boolean literal or a variable reference.
     */
    public static IfClause ifClauseWithElse(ASTNode condition,
                                            ASTNode[] ifBodyNodes,
                                            ASTNode[] elseBodyNodes) {
        if (!(condition instanceof BoolLiteral || condition instanceof VariableReference)) {
            throw new IllegalArgumentException("Condition must be a boolean or variable reference");
        }

        IfClause clause = new IfClause();
        clause.addChild(condition);

        if (ifBodyNodes != null) {
            for (ASTNode node : ifBodyNodes) {
                clause.addChild(node);
            }
        }

        ElseClause elseClause = new ElseClause();
        if (elseBodyNodes != null) {
            for (ASTNode node : elseBodyNodes) {
                elseClause.addChild(node);
            }
        }

        clause.addChild(elseClause);
        return clause;
    }

    /**
     * Creates an {@code IfClause} object with a condition, a single node to execute if the condition is true,
     * and a list of nodes to execute if the condition is false.
     *
     * @param condition the conditional expression for the {@code IfClause}. This must be a boolean literal
     *                  or a variable reference; otherwise, an {@code IllegalArgumentException} will be thrown.
     * @param ifBody a single {@code ASTNode} representing the body to execute when the condition is true.
     *               Can be {@code null}, in which case no nodes are added for the "if" branch.
     * @param elseBodyNodes an array of {@code ASTNode} objects representing the body to execute when the condition is false.
     *                      Can be {@code null}, in which case no nodes are added for the "else" branch.
     * @return an {@code IfClause} object containing the condition, "if" body node, and optional "else" body nodes.
     * @throws IllegalArgumentException if the condition is not a boolean literal or a variable reference.
     */
    public static IfClause ifClauseWithElse(ASTNode condition,
                                            ASTNode ifBody,
                                            ASTNode... elseBodyNodes) {
        ASTNode[] ifArr = (ifBody == null) ? new ASTNode[0] : new ASTNode[]{ ifBody };
        ASTNode[] elseArr = (elseBodyNodes == null) ? new ASTNode[0] : elseBodyNodes;
        return ifClauseWithElse(condition, ifArr, elseArr);
    }

    /**
     * Creates an {@code AST} object with a single style rule containing one property declaration.
     * The rule is identified by a tag name, and the property declaration consists of a property name and its corresponding expression value.
     *
     * @param ruleTag the name of the tag that represents the rule. Must not be null.
     * @param propertyName the name of the property to be declared in the style rule. Must not be null.
     * @param expression the value of the property, represented as an {@code Expression} object. Must not be null.
     * @return an {@code AST} object representing the stylesheet containing the specified rule and property declaration.
     */
    public static AST ruleWithPropertyDeclaration(String ruleTag, String propertyName, Expression expression) {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule(ruleTag,
                        ASTBuilder.decl(propertyName, expression)
                )
        );
    }

}
