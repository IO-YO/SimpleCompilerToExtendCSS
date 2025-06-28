package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
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
     * If no errors are found, multiple errors are present, or the error message
     * does not contain the expected parts, the assertion fails.
     *
     * @param ast The abstract syntax tree (AST) to check for semantic errors.
     * @param expectedParts A variable-length list of expected substrings that should
     *                      be present in the error message.
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

        String actual = errors.get(0).toString();
        for (String part : expectedParts) {
            assertTrue(actual.contains(part),
                    "Expected error message to contain: \"" + part + "\"\nActual: \"" + actual + "\"");
        }
    }

    /**
     * Helper method to assert that there are no errors in the AST.
     * It checks that the list of errors is empty.
     *
     * @param ast The AST to check for errors.
     */
    private void assertNoErrors(AST ast) {
        List<SemanticError> errors = getCleanErrors(ast);
        if (!errors.isEmpty()) {
            fail("Expected no errors, but got: " + errors.stream()
                    .map(SemanticError::toString)
                    .collect(Collectors.joining(", ")));
        }
    }

    /**
     * Helper method to retrieve and clean the list of errors from the AST.
     * It filters out any null errors and returns a list of non-null SemanticError objects.
     *
     * @param ast The AST from which to retrieve errors.
     * @return A list of non-null SemanticError objects.
     */
    private List<SemanticError> getCleanErrors(AST ast) {
        return ast.getErrors()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Test
    void testUndefinedVariable() {
        AST ast = checkFixture(Fixtures.undefinedVariable());
        assertSingleError(ast, "ERROR", "Unknown variable", "DefaultWidth");
    }

    @Test
    void testDefinedVariable() {
        AST ast = checkFixture(Fixtures.definedVariable());
        assertNoErrors(ast);
    }

    @Test
    void testInvalidWidthExpression() {
        String propertyWidth = "width";
        ScalarLiteral literal = new ScalarLiteral(10);
        AST ast = checkFixture(Fixtures.propertyDeclaration("p", propertyWidth, literal));
        assertSingleError(ast, "ERROR", "SCALAR", propertyWidth);
    }

    @Test
    void testValidWidthDeclaration() {
        AST ast = checkFixture(Fixtures.propertyDeclaration("p", "width", new PixelLiteral("100")));
        assertNoErrors(ast);
    }

    @Test
    void TestDefinedVariableReferencedWithWrongType() {
        AST ast = checkFixture(Fixtures.definedVariableReferencedWithWrongType());
        assertSingleError(ast,
                "ERROR:",
                "PIXEL",
                "color"
        );
    }

    @Test
    void testVariableDeclaredInIf_thenUsedOutside_shouldFail() {
        AST ast = checkFixture(Fixtures.variableDeclaredInsideIf_thenUsedOutside_shouldFail());
        assertSingleError(ast, "ERROR", "Unknown variable");
    }

    @Test
    void testVariableDeclaredInElse_thenUsedOutside_shouldFail() {
        AST ast = checkFixture(Fixtures.variableDeclaredInsideElse_thenUsedOutside_shouldFail());
        assertSingleError(ast, "ERROR", "Unknown variable");
    }

    @Test
    void testVariableDeclaredOutsideIf_thenUsedInside_shouldSucceed() {
        AST ast = checkFixture(Fixtures.variableDeclaredOutsideIf_thenUsedInside_shouldSucceed());
        assertNoErrors(ast);
    }

    @Test
    void testVariableDeclaredWithAnotherVariable_Correct() {
        AST ast = checkFixture(Fixtures.variableDeclaredWithAnotherVariable_Correct());
        assertNoErrors(ast);
    }

    @Test
    void testVariableDeclaredWithAnotherVariable_Incorrect() {
        AST ast = checkFixture(Fixtures.variableDeclaredWithAnotherVariable_Incorrect());
        assertSingleError(ast, "ERROR", "NotDeclaredVariable");
    }

    @Test
    void test_ifStatementWithBoolean_Correct() {
        AST ast = checkFixture(Fixtures.ifStatementWithBoolean_Correct());
        assertNoErrors(ast);
    }

    @Test
    void test_ifStatementWithScalar_Incorrect() {
        AST ast = checkFixture(Fixtures.ifStatementWithScalar_incorrect());
        assertSingleError(ast, "ERROR", "SCALAR", "If-condition");
    }

    @Test
    void test_IfConditionWithVariableRef_Correct() {
        AST ast = checkFixture(Fixtures.ifStatementVariableRef_Correct());
        assertNoErrors(ast);
    }

    @Test
    void test_IfConditionWithVariableRef_Incorrect() {
        AST ast = checkFixture(Fixtures.ifStatementVariableRef_Incorrect());
        assertSingleError(ast, "ERROR", "If-condition");
    }

    @Test
    void testAddScalarAndScalar_valid() {
        AST ast = checkFixture(Fixtures.addScalarAndScalar());
        assertNoErrors(ast);
    }

    @Test
    void testAddPixelAndPixel_valid() {
        AST ast = checkFixture(Fixtures.addPixelAndPixel());
        assertNoErrors(ast);
    }

    @Test
    void testAddPixelAndPercentage_invalid() {
        AST ast = checkFixture(Fixtures.addPixelAndPercentage());
        assertSingleError(ast, "Add", "PIXEL", "PERCENTAGE");
    }

    @Test
    void testSubtractPercentageAndScalar_invalid() {
        AST ast = checkFixture(Fixtures.subtractPercentageAndScalar());
        assertSingleError(ast, "Subtract", "PERCENTAGE", "SCALAR");
    }

    @Test
    void testSubtractScalarAndScalar_valid() {
        AST ast = checkFixture(Fixtures.subtractScalarAndScalar());
        assertNoErrors(ast);
    }

    @Test
    void testMultiplyScalarAndPixel_valid() {
        AST ast = checkFixture(Fixtures.multiplyScalarAndPixel());
        assertNoErrors(ast);
    }

    @Test
    void testMultiplyPixelAndScalar_valid() {
        AST ast = checkFixture(Fixtures.multiplyPixelAndScalar());
        assertNoErrors(ast);
    }

    @Test
    void testMultiplyPixelAndPixel_invalid() {
        AST ast = checkFixture(Fixtures.multiplyPixelAndPixel());
        assertSingleError(ast, "Multiply", "PIXEL", "PIXEL");
    }

    @Test
    void testAddColorAndPixel_invalid() {
        AST ast = checkFixture(Fixtures.addColorAndPixel());
        assertSingleError(ast, "Color", "operation");
    }

    @Test
    void testNestedComplexOperation_invalid() {
        AST ast = checkFixture(Fixtures.nestedComplexOperation());
        assertSingleError(ast, "Subtract", "PIXEL", "SCALAR");
    }
}