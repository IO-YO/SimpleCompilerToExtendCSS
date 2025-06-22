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
}
