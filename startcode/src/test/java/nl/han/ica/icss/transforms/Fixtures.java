package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public class Fixtures {


    public record ASTPair(AST input, AST expected) {}

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

    public static ASTPair expressionEval_addPixels() {
        AST input = ASTBuilder.ruleWithPropertyDeclaration(
                "p", "width",
                new AddOperation(new PixelLiteral(7), new PixelLiteral(3)));

        AST expected = ASTBuilder.ruleWithPropertyDeclaration(
                "p", "width",
                new PixelLiteral(10));

        return new ASTPair(input, expected);
    }

    public static ASTPair expressionEval_multiplyScalarPercentage() {
        AST input = ASTBuilder.ruleWithPropertyDeclaration(
                "div", "height",
                new MultiplyOperation(new ScalarLiteral(4), new PercentageLiteral(5)));

        AST expected = ASTBuilder.ruleWithPropertyDeclaration(
                "div", "height",
                new PercentageLiteral(20));

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

    public static ASTPair expressionEval_subtractPercentages() {
        // 90% - 40% = 50%
        AST in = ASTBuilder.ruleWithPropertyDeclaration(
                "p", "height",
                new SubtractOperation(
                        new PercentageLiteral(90),
                        new PercentageLiteral(40)
                )
        );
        AST exp = ASTBuilder.ruleWithPropertyDeclaration(
                "p", "height", new PercentageLiteral(50));
        return new ASTPair(in, exp);
    }

    public static ASTPair expressionEval_pixelTimesScalar() {
        // 4 * 8px = 32px
        AST in = ASTBuilder.ruleWithPropertyDeclaration(
                "div", "width",
                new MultiplyOperation(
                        new ScalarLiteral(4), new PixelLiteral(8))
        );
        AST exp = ASTBuilder.ruleWithPropertyDeclaration(
                "div", "width", new PixelLiteral(32));
        return new ASTPair(in, exp);
    }

    public static ASTPair expressionEval_precedence_pxPlusScalarTimesPx() {
        // 10px + 2 * 5px  = 20px
        AST in = ASTBuilder.ruleWithPropertyDeclaration(
                "h1", "width",
                new AddOperation(
                        new PixelLiteral(10),
                        new MultiplyOperation(
                                new ScalarLiteral(2),
                                new PixelLiteral(5)
                        )
                )
        );
        AST exp = ASTBuilder.ruleWithPropertyDeclaration(
                "h1", "width", new PixelLiteral(20));
        return new ASTPair(in, exp);
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
