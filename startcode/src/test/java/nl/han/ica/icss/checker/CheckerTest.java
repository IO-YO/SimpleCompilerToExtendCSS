package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;


import java.util.Formattable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CheckerTest {

    public CheckerTest() {

    }

    /**
     * Helper method to assert that a single error is present in the AST.
     * It checks that there is exactly one error and that its message matches the expected message.
     *
     * @param ast The AST to check for errors.
     * @param expectedMessage The expected error message.
     */
    private void assertSingleError(AST ast, String expectedMessage) {
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
        assertEquals("ERROR: " + expectedMessage, actual);
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
        assertSingleError(ast, "Variable is not defined.");
    }

    @Test
    void testInvalidWidthExpression() {
        AST ast = checkFixture(Fixtures.propertyDeclaration("p", "width", new ScalarLiteral("10")));
        assertSingleError(ast, "Property 'width' can't be a 'Scalar literal (10)'");
    }

    @Test
    void testValidWidthDeclaration() {
        AST ast = checkFixture(Fixtures.propertyDeclaration("p", "width", new PixelLiteral("100")));
        assertNoErrors(ast);
    }

    private AST checkFixture(AST ast) {
        Checker checker = new Checker();
        checker.check(ast);
        return ast;
    }

}