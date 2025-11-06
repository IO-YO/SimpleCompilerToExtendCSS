package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.han.ica.icss.ASTBuilder.ASTBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checker")
class CheckerTest {

    private AST checkFixture(AST ast) {
        Checker checker = new Checker();
        checker.check(ast);
        return ast;
    }

    private void assertSingleError(AST ast, String... expectedParts) {
        List<SemanticError> errors = getCleanErrors(ast);

        if (errors.isEmpty()) {
            fail("Expected one semantic error, but got none.");
        }

        if (errors.size() > 1) {
            String allErrors = errors.stream()
                    .map(SemanticError::toString)
                    .collect(Collectors.joining(", "));
            fail("Expected one semantic error, but got " + errors.size() + ": " + allErrors);
        }

        String actual = errors.getFirst().toString();
        for (String part : expectedParts) {
            assertTrue(actual.contains(part),
                    "Expected error message to contain: \"" + part + "\"\nActual: \"" + actual + "\"");
        }
    }

    private void assertNoErrors(AST ast) {
        List<SemanticError> errors = getCleanErrors(ast);
        if (!errors.isEmpty()) {
            fail("Expected no errors, but got: " + errors.stream()
                    .map(SemanticError::toString)
                    .collect(Collectors.joining(", ")));
        }
    }

    private List<SemanticError> getCleanErrors(AST ast) {
        return ast.getErrors()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("CH04: Property type checking")
    class CH04_Properties {

        record Case(String property, String valueKind, Expression value, boolean valid) {
        }

        Stream<Case> cases() {
            return Stream.of(
                    new Case("width", "PIXEL", px(10), true),
                    new Case("width", "PERCENTAGE", percent(10), true),
                    new Case("width", "SCALAR", scalar(10), false),
                    new Case("width", "COLOR", color("#ff0"), false)
            );
        }

        @ParameterizedTest
        @MethodSource("cases")
        void property_matrix(Case c) {
            AST ast = checkFixture(styleSheet(rule("p", decl(c.property, c.value))));
            if (c.valid) assertNoErrors(ast);
            else assertSingleError(ast, c.property);
        }
    }

    @Tag("CH01")
    @DisplayName("CH01: Undefined variable is rejected")
    @Test
    void CH01_UndefinedVariable_FailsWhenNotDeclared() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", varRef("DefaultWidth")))
                )
        );
        assertSingleError(ast, "ERROR", "Unknown variable", "DefaultWidth");
    }

    @Tag("CH01")
    @DisplayName("CH01: Referencing a declared variable succeeds")
    @Test
    void CH01_VariableDeclared_SucceedsWhenReferenced() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("DefaultWidth", px(10)),
                        rule("p", decl("width", varRef("DefaultWidth")))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH01")
    @DisplayName("CH01: Variable assignment can reference previously declared variable (correct)")
    @Test
    void CH01_VariableDeclaredWithAnotherVariable_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("FirstVar", px(10)),
                        varAssignment("SecondVar", varRef("FirstVar"))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH01")
    @DisplayName("CH01: Variable assignment referencing undeclared variable fails")
    @Test
    void CH01_VariableDeclaredWithAnotherVariable_FailsWhenRefUndefined() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("FirstVar", varRef("NotDeclaredVariable"))
                )
        );
        assertSingleError(ast, "ERROR", "NotDeclaredVariable");
    }

    @Tag("CH04")
    @DisplayName("CH04: Scalar assigned to width is invalid")
    @Test
    void CH04_PropertyTypeMismatch_ScalarInWidth_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", scalar(10)))
                )
        );
        assertSingleError(ast, "ERROR", "SCALAR", "width");
    }

    @Tag("CH04")
    @DisplayName("CH05: Scalar assigned to width in If condition is invalid")
    @Test
    void CH05_IfCondition_ScalarAssignedToWidth_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", ifClause(bool(true), decl("width", scalar(10))))
                )
        );
        assertSingleError(ast, "ERROR", "SCALAR");
    }

    @Tag("CH04")
    @DisplayName("CH04: Pixel assigned to width is valid")
    @Test
    void CH04_PropertyTypeMatch_PixelInWidth_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", px(100)))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH04")
    @DisplayName("CH04: Variable of wrong type used in property fails (PIXEL to color)")
    @Test
    void CH04_PropertyTypeMismatch_VariableWrongTypeInProperty_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("DefaultWidth", px(10)),
                        rule("p", decl("color", varRef("DefaultWidth")))
                )
        );
        assertSingleError(ast, "ERROR:", "PIXEL", "color");
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable declared in if must not leak outside (if branch)")
    @Test
    void CH06_VarDeclaredInsideIf_UsedOutside_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p",
                                ifClause(bool(true), varAssignment("ScopedVar", px(10))),
                                decl("width", varRef("ScopedVar"))
                        )
                )
        );
        assertSingleError(ast, "ERROR", "Unknown variable");
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable declared in else must not leak outside (else branch)")
    @Test
    void CH06_VarDeclaredInsideElse_UsedOutside_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p",
                                ifElseClause(bool(true), varAssignment("ScopedElseVar", px(20))),
                                decl("width", varRef("ScopedElseVar"))
                        )
                )
        );
        assertSingleError(ast, "ERROR", "Unknown variable");
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable declared outside if is usable inside")
    @Test
    void CH06_VarDeclaredOutsideIf_UsedInside_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("GlobalWidth", px(15)),
                        rule("p",
                                ifClause(bool(true), decl("width", varRef("GlobalWidth")))
                        )
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable redeclared")
    @Test
    void CH06_VarReDeclared_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("GlobalWidth", px(15)),
                        varAssignment("GlobalWidth", px(15))
                )
        );
        assertSingleError(ast, "ERROR", "Variable", "GlobalWidth");
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable redeclared in if clause FAILS")
    @Test
    void CH06_VarReDeclared_in_ifClause_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("GlobalWidth", px(15)),
                        rule("p",
                                ifClause(bool(true), varAssignment("GlobalWidth", px(15)))
                        )
                )
        );
        assertSingleError(ast, "ERROR", "Variable", "GlobalWidth");
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with boolean literal is valid")
    @Test
    void CH05_IfCondition_WithBooleanLiteral_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", ifClause(bool(true), decl("width", px(10))))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with scalar is invalid")
    @Test
    void CH05_IfCondition_WithScalar_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", ifClause(scalar(10), decl("width", px(10))))
                )
        );
        assertSingleError(ast, "ERROR", "SCALAR", "If-condition");
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with boolean variable reference is valid")
    @Test
    void CH05_IfCondition_WithVariableRef_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("LightMode", bool(true)),
                        rule("p", ifClause(varRef("LightMode"), decl("width", px(10))))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with non-boolean variable reference is invalid")
    @Test
    void CH05_IfCondition_WithVariableRef_FailsWhenNotBoolean() {
        AST ast = checkFixture(
                styleSheet(
                        varAssignment("LightMode", px(10)),
                        rule("p", ifClause(varRef("LightMode"), decl("width", px(10))))
                )
        );
        assertSingleError(ast, "ERROR", "If-condition");
    }

    @Tag("CH02")
    @DisplayName("CH02: Add pixel + pixel is valid")
    @Test
    void CH02_Add_PixelPlusPixel_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", addition(px(10), px(5))))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Add pixel + percentage is invalid")
    @Test
    void CH02_Add_PixelPlusPercentage_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", addition(px(10), percent(5))))
                )
        );
        assertSingleError(ast, "Add", "PIXEL", "PERCENTAGE");
    }

    @Tag("CH02")
    @DisplayName("CH02: Subtract percentage - scalar is invalid")
    @Test
    void CH02_Subtract_PercentageMinusScalar_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", subtract(percent(10), scalar(2))))
                )
        );
        assertSingleError(ast, "Subtract", "PERCENTAGE", "SCALAR");
    }

    @Tag("CH02")
    @DisplayName("CH02: Subtract scalar - scalar is valid")
    @Test
    void CH02_Subtract_ScalarMinusScalar_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", subtract(scalar(5), scalar(2))))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Multiply scalar * pixel is valid")
    @Test
    void CH02_Multiply_ScalarTimesPixel_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", multiply(scalar(2), px(10))))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Multiply pixel * scalar is valid")
    @Test
    void CH02_Multiply_PixelTimesScalar_Succeeds() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", multiply(px(10), scalar(2))))
                )
        );
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Multiply pixel * pixel is invalid")
    @Test
    void CH02_Multiply_PixelTimesPixel_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("width", multiply(px(10), px(5))))
                )
        );
        assertSingleError(ast, "Multiply", "PIXEL", "PIXEL");
    }

    @Tag("CH02")
    @DisplayName("CH02: Nested complex operation with type mismatch is invalid (Subtract PIXEL, SCALAR)")
    @Test
    void CH02_NestedComplexOperation_TypeMismatch_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p",
                                decl("width",
                                        addition(
                                                subtract(
                                                        multiply(px(10), scalar(10)),
                                                        scalar(5)
                                                ),
                                                scalar(2)
                                        )
                                )
                        )
                )
        );
        assertSingleError(ast, "Subtract", "PIXEL", "SCALAR");
    }

    @Tag("CH03")
    @DisplayName("CH03: Any arithmetic operation involving color is invalid")
    @Test
    void CH03_ColorInOperation_AddColorAndPixel_Fails() {
        AST ast = checkFixture(
                styleSheet(
                        rule("p", decl("color", addition(color("#ff0000"), px(10))))
                )
        );
        assertSingleError(ast, "Color", "operation");
    }
}
