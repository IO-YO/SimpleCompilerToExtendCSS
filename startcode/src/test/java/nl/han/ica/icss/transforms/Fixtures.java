package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

public class Fixtures {


    public record ASTPair(AST input, AST expected) {
    }

    public static ASTPair createExpressionEvalPair(Expression expression, Literal expectedLiteral) {
        AST input = ASTBuilder.ruleWithPropertyDeclaration(
                "p",
                "width",
                expression
        );

        AST expected = ASTBuilder.ruleWithPropertyDeclaration(
                "p",
                "width",
                expectedLiteral
        );

        return new ASTPair(input, expected);

    }

    private static ASTNode[] concat(ASTNode[] a, ASTNode[] b) {
        ASTNode[] out = new ASTNode[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    /**
     * Creates a pair of ASTs representing a conditional rule. Constructs an input AST
     * with a conditional clause embedded within a rule and an expected AST to evaluate
     * the transformation result.
     *
     * @param condition the conditional expression for the rule. Determines the condition
     *                  under which the rule applies.
     * @param prefix an array of AST nodes to be placed before the conditional clause in the rule.
     * @param ifBody an array of AST nodes to represent the "if" body of the conditional clause.
     * @param elseBodyOrNull an optional array of AST nodes to represent the "else" body, if any.
     *                       May be null if no "else" clause is present.
     * @param suffix an array of AST nodes to be placed after the conditional clause in the rule.
     * @param expectedBody an array of AST nodes representing the expected outcome or structure
     *                     of the rule's transformation.
     * @return an ASTPair object containing the input AST and the expected AST.
     */
    public static ASTPair createConditionalRulePair(Expression condition,
                                                    ASTNode[] prefix,
                                                    ASTNode[] ifBody,
                                                    ASTNode[] elseBodyOrNull,
                                                    ASTNode[] suffix,
                                                    ASTNode[] expectedBody) {

        // If there is no else body, we create a simple if clause
        ASTNode ifNode = (elseBodyOrNull == null)
                ? ASTBuilder.ifClause(condition, ifBody)
                : ASTBuilder.ifClauseWithElse(condition, ifBody, elseBodyOrNull);

        // Concatenate the prefix, ifNode, and suffix to form the full body
        ASTNode[] withIf   = concat(prefix, new ASTNode[]{ ifNode });
        ASTNode[] fullBody = concat(withIf, suffix);

        AST input = ASTBuilder.stylesheet(ASTBuilder.rule("p", fullBody));
        AST expected = ASTBuilder.stylesheet(ASTBuilder.rule("p", expectedBody));
        return new ASTPair(input, expected);
    }

    public static ASTPair createConditionalRulePair(Expression condition,
                                                    ASTNode[] prefix,
                                                    ASTNode[] ifBody,
                                                    ASTNode[] suffix,
                                                    ASTNode[] expectedBody) {
        return createConditionalRulePair(condition, prefix, ifBody, null, suffix, expectedBody);
    }
    public static ASTPair createConditionalRulePair(Expression condition,
                                                    ASTNode[] ifBody,
                                                    ASTNode[] expectedBody) {
        return createConditionalRulePair(condition, new ASTNode[0], ifBody, null, new ASTNode[0], expectedBody);
    }
}
