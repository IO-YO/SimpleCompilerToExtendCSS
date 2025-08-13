package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

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

    public static ASTPair createIfWidthPair(
            Expression condition,
            Literal thenWidth,
            Literal elseWidthOrNull,
            Literal expectedWidthOrNull
    ) {
        // Build the if-body
        ASTNode thenDecl = ASTBuilder.decl("width", thenWidth);

        // Input: stylesheet([prelude...], rule(p { if [cond] { width: then } else { width: else? } }))
        AST input = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        elseWidthOrNull == null
                                ? ASTBuilder.ifClause(condition, thenDecl)
                                : ASTBuilder.ifClauseWithElse(
                                condition,
                                thenDecl,
                                ASTBuilder.decl("width", elseWidthOrNull))
                )
        );

        // Expected: stylesheet(rule(p { width: expected? }))
        AST expected = (expectedWidthOrNull == null)
                ? ASTBuilder.stylesheet(ASTBuilder.rule("p"))
                : ASTBuilder.stylesheet(
                ASTBuilder.rule("p", ASTBuilder.decl("width", expectedWidthOrNull))
        );

        return new ASTPair(input, expected);
    }

    // --- NEW: helper to quickly make `X := <bool>` assignments for prelude ---
    public static VariableAssignment assignBool(String name, boolean value) {
        return ASTBuilder.assign(name, new BoolLiteral(value));
    }

    private static AST createASTPairForIfElseClauses(
            Expression condition,
            Declaration... ifBody
    ) {
        AST input = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClauseWithElse(
                                condition,
                                ifBody
                        )
                )
        );

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ifBody, // if body is always kept
                        elseBody // else body is always kept
                )
        );

        return new ASTPair(input, expected);
    })

    private static AST ifClause_Simple(boolean condition) {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(
                                new BoolLiteral(condition),
                                ASTBuilder.decl(
                                        "width",
                                        new PixelLiteral(10)
                                )
                        )
                )
        );
    }


    public static ASTPair ifClause_TrueCase() {
        AST input = ifClause_Simple(true);

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.decl("width", new PixelLiteral(10))
                )
        );

        return new ASTPair(input, expected);
    }
    public static ASTPair ifClause_FalseCase() {
        AST input = ifClause_Simple(false);

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p")
        );

        return new ASTPair(input, expected);
    }


    public static AST ifClause_VariableAssignment_Simple(boolean bool) {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("c", new BoolLiteral(bool)),
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(
                                new VariableReference("c"),
                                ASTBuilder.decl(
                                        "width",
                                        new PixelLiteral(10)
                                )
                        )
                )
        );

    }

    public static ASTPair ifClause_VariableAssignment_TrueCase() {
        AST input = ifClause_VariableAssignment_Simple(true);

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.decl("width", new PixelLiteral(10))
                )
        );

        return new ASTPair(input, expected);
    }

    public static ASTPair ifClause_VariableAssignment_FalseCase() {
        AST input = ifClause_VariableAssignment_Simple(false);

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p")
        );

        return new ASTPair(input, expected);
    }


    public static ASTPair variableTransform_globalVar_refToRuleBodyDecl() {

        AST input = ASTBuilder.stylesheet(
                ASTBuilder.assign("DefaultWidth", new PixelLiteral(42)),
                ASTBuilder.rule("p",
                        ASTBuilder.declVar("width", "DefaultWidth")
                )
        );

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.decl("width", new PixelLiteral(42))
                )
        );

        return new ASTPair(input, expected);
    }

    public static ASTPair variableTransform_scopedVarInStyleRule_refToLiteral() {
        AST input = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.assign("WidthVar", new PixelLiteral(10)),
                        ASTBuilder.declVar("width", "WidthVar")
                )
        );

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.decl("width", new PixelLiteral(10))
                )
        );

        return new ASTPair(input, expected);
    }

    private static AST ifWithElse(ASTNode condition) {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClauseWithElse(
                                condition, // condition node
                                ASTBuilder.decl("color", new PixelLiteral(1)), // if body
                                ASTBuilder.decl("color", new PixelLiteral(2)) // else body
                        )
                )
        );
    }

    public static ASTPair ifWithElse_ifFalse_keepsElse() {
        AST in  = ifWithElse(new BoolLiteral(false));
        AST exp = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.decl("color", new PixelLiteral(2))   // else-body kept
                )
        );
        return new ASTPair(in, exp);
    }

    public static ASTPair ifIfElse_outerTrueKeepsInnerIf_innerFalseKeepsElse() {
        AST in = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(
                                new BoolLiteral(true),
                                ASTBuilder.ifClauseWithElse(
                                        new BoolLiteral(false), // inner condition
                                        ASTBuilder.decl("width", new PixelLiteral(1)),
                                        ASTBuilder.decl("width", new PixelLiteral(9))
                                )
                        )
                )
        );

        AST exp = ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.decl("width", new PixelLiteral(9))   // deepest else
                )
        );

        return new ASTPair(in, exp);
    }

}
