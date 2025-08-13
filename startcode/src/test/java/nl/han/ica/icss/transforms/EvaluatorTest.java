package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    public record EvalCase(String name, Expression input, Literal Expected) {
    }

    private void assertEvaluatedCorrectly(Fixtures.ASTPair test) {
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    // --- TR01: Expression Evaluation ---

    static Stream<Arguments> ExpressionAdditionCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "AddPixels",
                        new AddOperation(new PixelLiteral(1), new PixelLiteral(1)),
                        new PixelLiteral(2)
                )),
                Arguments.of(new EvalCase(
                        "AddPercentages",
                        new AddOperation(new PercentageLiteral(10), new PercentageLiteral(20)),
                        new PercentageLiteral(30)
                )),
                Arguments.of(new EvalCase(
                        "AddScalars",
                        new AddOperation(new ScalarLiteral(10), new ScalarLiteral(20)),
                        new ScalarLiteral(30)
                )),
                Arguments.of(new EvalCase(
                        "AddMultiplePixels",
                        new AddOperation(
                                new AddOperation(
                                        new PixelLiteral(5),
                                        new PixelLiteral(10)),
                                new PixelLiteral(15)
                        ),
                        new PixelLiteral(30)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionAdditionCases")
    @Tag("TR01")
    @DisplayName("TR01: Addition expressions are evaluated correctly")
    void TR01_Addition_EvaluatesCorrectly(EvalCase testCase) {
        Fixtures.ASTPair pair = Fixtures.createASTPairForLiteralExpression(testCase.input(), testCase.Expected());
        assertEvaluatedCorrectly(pair);
    }

    // --- TR01: Subtraction Evaluation ---

    static Stream<Arguments> ExpressionSubtractionCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "SubtractPixels",
                        new SubtractOperation(new PixelLiteral(10), new PixelLiteral(5)),
                        new PixelLiteral(5)
                )),
                Arguments.of(new EvalCase(
                        "SubtractPercentages",
                        new SubtractOperation(new PercentageLiteral(50), new PercentageLiteral(20)),
                        new PercentageLiteral(30)
                )),
                Arguments.of(new EvalCase(
                        "SubtractScalars",
                        new SubtractOperation(new ScalarLiteral(100), new ScalarLiteral(105)),
                        new ScalarLiteral(-5)
                )),
                Arguments.of(new EvalCase(
                        "SubtractMultiplePixels",
                        new SubtractOperation(
                                new SubtractOperation(
                                        new PixelLiteral(50),
                                        new PixelLiteral(20)),
                                new PixelLiteral(10)
                        ),
                        new PixelLiteral(20)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionSubtractionCases")
    @Tag("TR01")
    @DisplayName("TR01: Subtraction expressions are evaluated correctly")
    void TR01_Subtraction_EvaluatesCorrectly(EvalCase testCase) {
        Fixtures.ASTPair pair = Fixtures.createASTPairForLiteralExpression(testCase.input(), testCase.Expected());
        assertEvaluatedCorrectly(pair);
    }

    // --- TR01: Subtraction and Addition Evaluation ---

    /**
     * Tests that subtraction expressions are evaluated correctly.
     * This includes cases where the left-hand side is greater than, equal to, or less than the right-hand side.
     */

    static Stream<Arguments> ExpressionSubtractAndAdditionCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "SubtractAndAddPixels",
                        new AddOperation(
                                new SubtractOperation(new PixelLiteral(20), new PixelLiteral(10)),
                                new PixelLiteral(5)
                        ),
                        new PixelLiteral(15)
                )),
                Arguments.of(new EvalCase(
                        "SubtractAndAddPercentages",
                        new AddOperation(
                                new SubtractOperation(new PercentageLiteral(30), new PercentageLiteral(10)),
                                new PercentageLiteral(5)
                        ),
                        new PercentageLiteral(25)
                )),
                Arguments.of(new EvalCase(
                        "SubtractAndAddScalars",
                        new AddOperation(
                                new SubtractOperation(new ScalarLiteral(50), new ScalarLiteral(20)),
                                new ScalarLiteral(10)
                        ),
                        new ScalarLiteral(40)
                )),
                Arguments.of(
                        new EvalCase(
                                "SubtractAndAddMultiplePixels",
                                new AddOperation(
                                        new SubtractOperation(
                                                new AddOperation(
                                                        new PixelLiteral(30),
                                                        new PixelLiteral(20)),
                                                new PixelLiteral(10)
                                        ),
                                        new PixelLiteral(5)
                                ),
                                new PixelLiteral(45)
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionSubtractAndAdditionCases")
    @Tag("TR01")
    @DisplayName("TR01: Subtraction and addition expressions are evaluated correctly")
    void TR01_SubtractAndAdd_EvaluatesCorrectly(EvalCase testCase) {
        Fixtures.ASTPair pair = Fixtures.createASTPairForLiteralExpression(testCase.input(), testCase.Expected());
        assertEvaluatedCorrectly(pair);
    }


    @Test
    @Tag("TR01")
    @DisplayName("TR01: Multiply scalar and percentage is evaluated correctly")
    void TR01_MultiplyScalarAndPercentage_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_multiplyScalarPercentage());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Multiply scalar and pixel is evaluated correctly")
    void TR01_MultiplyScalarAndPixel_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_pixelTimesScalar());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Subtract percentage values is evaluated correctly")
    void TR01_SubtractPercentages_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_subtractPercentages());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Precedence respected in mixed add/multiply expressions")
    void TR01_AddMultiplyPrecedence_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_precedence_pxPlusScalarTimesPx());
    }

    // --- TR02: If/Else Evaluation ---

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with true literal includes its body")
    void TR02_IfTrue_IncludesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_TrueCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with false literal removes its body")
    void TR02_IfFalse_RemovesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_FalseCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with true variable includes its body")
    void TR02_IfVarTrue_IncludesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_VariableAssignment_TrueCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with false variable removes its body")
    void TR02_IfVarFalse_RemovesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_VariableAssignment_FalseCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If/Else clause with false condition keeps else body")
    void TR02_IfWithElse_ConditionFalse_KeepsElseBody() {
        assertEvaluatedCorrectly(Fixtures.ifWithElse_ifFalse_keepsElse());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: Nested if/else applies both outer true and inner false conditions")
    void TR02_NestedIfElse_OuterTrue_InnerFalse_KeepsInnerElse() {
        assertEvaluatedCorrectly(Fixtures.ifIfElse_outerTrueKeepsInnerIf_innerFalseKeepsElse());
    }

    // --- Shared: Variable replacement ---

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Global variable reference is replaced by literal")
    void TR01_GlobalVarReference_ReplacedWithLiteral() {
        assertEvaluatedCorrectly(Fixtures.variableTransform_globalVar_refToRuleBodyDecl());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Scoped variable in rule is replaced by literal")
    void TR01_ScopedVarInRule_ReplacedWithLiteral() {
        assertEvaluatedCorrectly(Fixtures.variableTransform_scopedVarInStyleRule_refToLiteral());
    }
}
