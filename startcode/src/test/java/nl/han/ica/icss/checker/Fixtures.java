package nl.han.ica.icss.checker;

import nl.han.ica.icss.ASTBuilder;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public class Fixtures {

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
                        ASTBuilder.ifClause(new BoolLiteral(true),
                                ASTBuilder.assign("ScopedVar", new PixelLiteral(10))
                        ),
                        ASTBuilder.declVar("width", "ScopedVar")
                )
        );
    }

    public static AST variableDeclaredInsideElse_thenUsedOutside_shouldFail() {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClauseWithElse(
                                new BoolLiteral(true),
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
                        ASTBuilder.ifClause(new BoolLiteral(true),
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

    public static AST ifStatementWithBoolean_Correct() {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(new BoolLiteral(true),
                                ASTBuilder.decl("width", new PixelLiteral(10))
                                )
                )
        );
    }

    public static AST ifStatementWithScalar_incorrect() {
        return ASTBuilder.stylesheet(
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(new ScalarLiteral(10),
                                ASTBuilder.decl("width", new PixelLiteral(10))
                        )
                )
        );
    }

    public static AST ifStatementVariableRef_Correct() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("LightMode", new BoolLiteral(true)),
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(new VariableReference("LightMode"),
                                ASTBuilder.decl("width", new PixelLiteral(10))
                        )
                )
        );
    }

    public static AST ifStatementVariableRef_Incorrect() {
        return ASTBuilder.stylesheet(
                ASTBuilder.assign("LightMode", new PixelLiteral(10)),
                ASTBuilder.rule("p",
                        ASTBuilder.ifClause(new VariableReference("LightMode"),
                                ASTBuilder.decl("width", new PixelLiteral(10))
                        )
                )
        );
    }

    public static AST addScalarAndScalar() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new AddOperation(new ScalarLiteral(10), new ScalarLiteral(5)));
    }

    public static AST addPixelAndPixel() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new AddOperation(new PixelLiteral(10), new PixelLiteral(5)));
    }

    public static AST addPixelAndPercentage() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new AddOperation(new PixelLiteral(10), new PercentageLiteral(5)));
    }

    public static AST subtractPercentageAndScalar() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new SubtractOperation(new PercentageLiteral(10), new ScalarLiteral(2)));
    }

    public static AST subtractScalarAndScalar() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new SubtractOperation(new ScalarLiteral(5), new ScalarLiteral(2)));
    }

    public static AST multiplyScalarAndPixel() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new MultiplyOperation(new ScalarLiteral(2), new PixelLiteral(10)));
    }

    public static AST multiplyPixelAndScalar() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new MultiplyOperation(new PixelLiteral(10), new ScalarLiteral(2)));
    }

    public static AST multiplyPixelAndPixel() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width",
                new MultiplyOperation(new PixelLiteral(10), new PixelLiteral(5)));
    }

    public static AST addColorAndPixel() {
        return ASTBuilder.ruleWithPropertyDeclaration("p", "color",
                new AddOperation(new ColorLiteral("#ff0000"), new PixelLiteral(10)));
    }

    public static AST nestedComplexOperation() {
        Expression expr =
                new AddOperation(
                        new SubtractOperation(
                                new MultiplyOperation(new PixelLiteral(10), new ScalarLiteral(10)),
                                new ScalarLiteral(5)
                        ),
                        new ScalarLiteral(2)
                );
        return ASTBuilder.ruleWithPropertyDeclaration("p", "width", expr);
    }

}
