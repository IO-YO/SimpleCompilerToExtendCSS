package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;

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


    public static AST ifClause_VariableAssignment_Simple(boolean c) {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("c", new BoolLiteral(c)),
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

    public static ASTPair addOperation_pixels() {
        AST input = ASTBuilder.ruleWithPropertyDeclaration(
                "p", "width",
                new AddOperation(new PixelLiteral(7), new PixelLiteral(3)));

        AST expected = ASTBuilder.ruleWithPropertyDeclaration(
                "p", "width",
                new PixelLiteral(10));

        return new ASTPair(input, expected);
    }

    public static ASTPair multiply_scalar_percentage() {
        AST input = ASTBuilder.ruleWithPropertyDeclaration(
                "div", "height",
                new MultiplyOperation(new ScalarLiteral(4), new PercentageLiteral(5)));

        AST expected = ASTBuilder.ruleWithPropertyDeclaration(
                "div", "height",
                new PercentageLiteral(20));

        return new ASTPair(input, expected);
    }

    public static ASTPair simpleVariableReplacement() {

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

    public static ASTPair scopedVariable() {
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


}
