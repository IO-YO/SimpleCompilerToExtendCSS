package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

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
                ASTBuilder.assign("c", new BoolLiteral(true)),
                ASTBuilder.rule("p",
                        ASTBuilder.decl("width", new PixelLiteral(10))
                )
        );

        return new ASTPair(input, expected);
    }

    public static ASTPair ifClause_VariableAssignment_FalseCase() {
        AST input = ifClause_VariableAssignment_Simple(false);

        AST expected = ASTBuilder.stylesheet(
                ASTBuilder.assign("c", new BoolLiteral(false)),
                ASTBuilder.rule("p")
        );

        return new ASTPair(input, expected);
    }

}
