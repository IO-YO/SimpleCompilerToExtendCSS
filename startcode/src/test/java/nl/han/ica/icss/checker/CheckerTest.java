package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CheckerTest {

    private AST checkFixture(AST ast) {
        Checker checker = new Checker();
        checker.check(ast);
        return ast;
    }

    /**
     * Asserts that there is exactly one semantic error in the provided AST
     * and that the error message contains the specified expected parts.
     */
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

    /** Helper to assert there are no errors. */
    private void assertNoErrors(AST ast) {
        List<SemanticError> errors = getCleanErrors(ast);
        if (!errors.isEmpty()) {
            fail("Expected no errors, but got: " + errors.stream()
                    .map(SemanticError::toString)
                    .collect(Collectors.joining(", ")));
        }
    }

    /** Helper to retrieve cleaned error list. */
    private List<SemanticError> getCleanErrors(AST ast) {
        return ast.getErrors()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ---------------------------
    // CH01: Undefined Variable
    // ---------------------------

    @Tag("CH01")
    @DisplayName("CH01: Undefined variable is rejected")
    @Test
    void CH01_UndefinedVariable_FailsWhenNotDeclared() {
        AST ast = checkFixture(Fixtures.undefinedVariable());
        assertSingleError(ast, "ERROR", "Unknown variable", "DefaultWidth");
    }

    @Tag("CH01")
    @DisplayName("CH01: Referencing a declared variable succeeds")
    @Test
    void CH01_VariableDeclared_SucceedsWhenReferenced() {
        AST ast = checkFixture(Fixtures.definedVariable());
        assertNoErrors(ast);
    }

    @Tag("CH01")
    @DisplayName("CH01: Variable assignment can reference previously declared variable (correct)")
    @Test
    void CH01_VariableDeclaredWithAnotherVariable_Succeeds() {
        AST ast = checkFixture(Fixtures.variableDeclaredWithAnotherVariable_Correct());
        assertNoErrors(ast);
    }

    @Tag("CH01")
    @DisplayName("CH01: Variable assignment referencing undeclared variable fails")
    @Test
    void CH01_VariableDeclaredWithAnotherVariable_FailsWhenRefUndefined() {
        AST ast = checkFixture(Fixtures.variableDeclaredWithAnotherVariable_Incorrect());
        assertSingleError(ast, "ERROR", "NotDeclaredVariable");
    }

    // CH04: Property Type Matching

    @Tag("CH04")
    @DisplayName("CH04: Scalar assigned to width is invalid")
    @Test
    void CH04_PropertyTypeMismatch_ScalarInWidth_Fails() {
        String propertyWidth = "width";
        ScalarLiteral literal = new ScalarLiteral(10);
        AST ast = checkFixture(Fixtures.propertyDeclaration("p", propertyWidth, literal));
        assertSingleError(ast, "ERROR", "SCALAR", propertyWidth);
    }

    @Tag("CH04")
    @DisplayName("CH04: Pixel assigned to width is valid")
    @Test
    void CH04_PropertyTypeMatch_PixelInWidth_Succeeds() {
        AST ast = checkFixture(Fixtures.propertyDeclaration("p", "width", new PixelLiteral("100")));
        assertNoErrors(ast);
    }

    @Tag("CH04")
    @DisplayName("CH04: Variable of wrong type used in property fails (PIXEL to color)")
    @Test
    void CH04_PropertyTypeMismatch_VariableWrongTypeInProperty_Fails() {
        AST ast = checkFixture(Fixtures.definedVariableReferencedWithWrongType());
        assertSingleError(ast, "ERROR:", "PIXEL", "color");
    }

    // CH06: Scope Handling

    @Tag("CH06")
    @DisplayName("CH06: Variable declared in if must not leak outside (if branch)")
    @Test
    void CH06_VarDeclaredInsideIf_UsedOutside_Fails() {
        AST ast = checkFixture(Fixtures.variableDeclaredInsideIf_thenUsedOutside_shouldFail());
        assertSingleError(ast, "ERROR", "Unknown variable");
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable declared in else must not leak outside (else branch)")
    @Test
    void CH06_VarDeclaredInsideElse_UsedOutside_Fails() {
        AST ast = checkFixture(Fixtures.variableDeclaredInsideElse_thenUsedOutside_shouldFail());
        assertSingleError(ast, "ERROR", "Unknown variable");
    }

    @Tag("CH06")
    @DisplayName("CH06: Variable declared outside if is usable inside")
    @Test
    void CH06_VarDeclaredOutsideIf_UsedInside_Succeeds() {
        AST ast = checkFixture(Fixtures.variableDeclaredOutsideIf_thenUsedInside_shouldSucceed());
        assertNoErrors(ast);
    }

    // CH05: If Condition Type Check

    @Tag("CH05")
    @DisplayName("CH05: If condition with boolean literal is valid")
    @Test
    void CH05_IfCondition_WithBooleanLiteral_Succeeds() {
        AST ast = checkFixture(Fixtures.ifStatementWithBoolean_Correct());
        assertNoErrors(ast);
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with scalar is invalid")
    @Test
    void CH05_IfCondition_WithScalar_Fails() {
        AST ast = checkFixture(Fixtures.ifStatementWithScalar_incorrect());
        assertSingleError(ast, "ERROR", "SCALAR", "If-condition");
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with boolean variable reference is valid")
    @Test
    void CH05_IfCondition_WithVariableRef_Succeeds() {
        AST ast = checkFixture(Fixtures.ifStatementVariableRef_Correct());
        assertNoErrors(ast);
    }

    @Tag("CH05")
    @DisplayName("CH05: If condition with non-boolean variable reference is invalid")
    @Test
    void CH05_IfCondition_WithVariableRef_FailsWhenNotBoolean() {
        AST ast = checkFixture(Fixtures.ifStatementVariableRef_Incorrect());
        assertSingleError(ast, "ERROR", "If-condition");
    }

    // CH02: Operand Type Checks (+, -, * rules)

    @Tag("CH02")
    @DisplayName("CH02: Add scalar + scalar is valid")
    @Test
    void CH02_Add_ScalarPlusScalar_Succeeds() {
        AST ast = checkFixture(Fixtures.addScalarAndScalar());
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Add pixel + pixel is valid")
    @Test
    void CH02_Add_PixelPlusPixel_Succeeds() {
        AST ast = checkFixture(Fixtures.addPixelAndPixel());
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Add pixel + percentage is invalid")
    @Test
    void CH02_Add_PixelPlusPercentage_Fails() {
        AST ast = checkFixture(Fixtures.addPixelAndPercentage());
        assertSingleError(ast, "Add", "PIXEL", "PERCENTAGE");
    }

    @Tag("CH02")
    @DisplayName("CH02: Subtract percentage - scalar is invalid")
    @Test
    void CH02_Subtract_PercentageMinusScalar_Fails() {
        AST ast = checkFixture(Fixtures.subtractPercentageAndScalar());
        assertSingleError(ast, "Subtract", "PERCENTAGE", "SCALAR");
    }

    @Tag("CH02")
    @DisplayName("CH02: Subtract scalar - scalar is valid")
    @Test
    void CH02_Subtract_ScalarMinusScalar_Succeeds() {
        AST ast = checkFixture(Fixtures.subtractScalarAndScalar());
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Multiply scalar * pixel is valid")
    @Test
    void CH02_Multiply_ScalarTimesPixel_Succeeds() {
        AST ast = checkFixture(Fixtures.multiplyScalarAndPixel());
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Multiply pixel * scalar is valid")
    @Test
    void CH02_Multiply_PixelTimesScalar_Succeeds() {
        AST ast = checkFixture(Fixtures.multiplyPixelAndScalar());
        assertNoErrors(ast);
    }

    @Tag("CH02")
    @DisplayName("CH02: Multiply pixel * pixel is invalid")
    @Test
    void CH02_Multiply_PixelTimesPixel_Fails() {
        AST ast = checkFixture(Fixtures.multiplyPixelAndPixel());
        assertSingleError(ast, "Multiply", "PIXEL", "PIXEL");
    }

    @Tag("CH02")
    @DisplayName("CH02: Nested complex operation with type mismatch is invalid (Subtract PIXEL, SCALAR)")
    @Test
    void CH02_NestedComplexOperation_TypeMismatch_Fails() {
        AST ast = checkFixture(Fixtures.nestedComplexOperation());
        assertSingleError(ast, "Subtract", "PIXEL", "SCALAR");
    }

    // CH03: Color Operation Check

    @Tag("CH03")
    @DisplayName("CH03: Any arithmetic operation involving color is invalid")
    @Test
    void CH03_ColorInOperation_AddColorAndPixel_Fails() {
        AST ast = checkFixture(Fixtures.addColorAndPixel());
        assertSingleError(ast, "Color", "operation");
    }
}
