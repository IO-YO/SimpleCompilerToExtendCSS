package nl.han.ica.icss.checker;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Fixtures {

    /**
     * Helper method to check if I didn't fumble the AST build
     * @param ast
     */
    private static void printAST(AST ast) {
        System.out.println(ast);
    }

    public static AST propertyDeclaration(String selector, String property, Expression expression) {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule(selector,
                        ASTBuilder.decl(property, expression)
                )
        );
    }
    public static AST undefinedVariable() {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.declVar("width", "DefaultWidth")
                )
        );
    }

    public static AST definedVariable() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("DefaultWidth", new PixelLiteral(10)),
                ASTBuilder.rule("p",
                        ASTBuilder.declVar("width", "DefaultWidth")
                        )
        );
    }

    public static AST definedVariableReferencedWithWrongType() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("DefaultWidth", new PixelLiteral(10)),
                ASTBuilder.rule("p",
                        ASTBuilder.declVar("color", "DefaultWidth")
                )
        );
    }

    public static AST variableDeclaredInsideIf_thenUsedOutside_shouldFail() {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(
                                ASTBuilder.assign("ScopedVar", new PixelLiteral(10))
                        ),
                        ASTBuilder.declVar("width", "ScopedVar") // ❌ should error
                )
        );
    }

    public static AST variableDeclaredInsideElse_thenUsedOutside_shouldFail() {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClauseWithElse(
                                null, // no if-body
                                ASTBuilder.assign("ScopedElseVar", new PixelLiteral(20))
                        ),
                        ASTBuilder.declVar("width", "ScopedElseVar")
                )
        );
    }

    public static AST variableDeclaredOutsideIf_thenUsedInside_shouldSucceed() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("GlobalWidth", new PixelLiteral(15)),
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(
                                ASTBuilder.declVar("width", "GlobalWidth")
                        )
                )
        );
    }

    public static AST variableDeclaredWithAnotherVariable_Correct() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("FirstVar", new PixelLiteral(10)),
                ASTBuilder.assign("SecondVar", new VariableReference("FirstVar"))
        );
    }

    public static AST variableDeclaredWithAnotherVariable_Incorrect() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("FirstVar", new VariableReference("NotDeclaredVariable")));
    }


}
