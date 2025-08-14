package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.*;

public class Fixtures {


    public record ASTPair(AST input, AST expected) {}

    public static ASTPair createASTPairForLiteralExpression(Expression expression, Literal expectedLiteral) {
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

    public static ASTPair createIfPair(Expression condition,
                                       ASTNode[] ifBody,
                                       ASTNode[] expectedBody) {
        AST input = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(condition, ifBody)
                )
        );
        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p", expectedBody)
        );
        return new ASTPair(input, expected);
    }

    public static ASTPair createIfElsePair(Expression condition,
                                           ASTNode[] ifBody,
                                           ASTNode[] elseBody,
                                           ASTNode[] expectedBody) {
        AST input = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClauseWithElse(condition, ifBody, elseBody)
                )
        );
        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p", expectedBody)
        );
        return new ASTPair(input, expected);
    }

}
