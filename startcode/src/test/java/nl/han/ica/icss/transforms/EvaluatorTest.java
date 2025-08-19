package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
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
        Fixtures.ASTPair pair = Fixtures.createExpressionEvalPair(testCase.input(), testCase.Expected());
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
        Fixtures.ASTPair pair = Fixtures.createExpressionEvalPair(testCase.input(), testCase.Expected());
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
        Fixtures.ASTPair pair = Fixtures.createExpressionEvalPair(testCase.input(), testCase.Expected());
        assertEvaluatedCorrectly(pair);
    }

    // --- TR01: Multiplication Evaluation ---
    static Stream<Arguments> ExpressionMultiplicationCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "MultiplyScalar",
                        new MultiplyOperation(new ScalarLiteral(4), new ScalarLiteral(5)),
                        new ScalarLiteral(20)
                )),
                Arguments.of(new EvalCase(
                        "MultiplyMultiplePixels",
                        new MultiplyOperation(
                                new MultiplyOperation(
                                        new ScalarLiteral(2),
                                        new PixelLiteral(3)),
                                new ScalarLiteral(4)
                        ),
                        new PixelLiteral(24)
                )),
                Arguments.of(new EvalCase(
                        "MultiplyScalarAndPercentage",
                        new MultiplyOperation(new ScalarLiteral(4), new PercentageLiteral(5)),
                        new PercentageLiteral(20)
                )),
                Arguments.of(new EvalCase(
                        "MultiplyPercentageAndScalar",
                        new MultiplyOperation(new PercentageLiteral(4), new ScalarLiteral(5)),
                        new PercentageLiteral(20)
                )),
                Arguments.of(new EvalCase(
                        "MultiplyPixelAndScalar",
                        new MultiplyOperation(new PixelLiteral(10), new ScalarLiteral(2)),
                        new PixelLiteral(20)
                )),
                Arguments.of(new EvalCase(
                        "MultiplyScalarAndPixel",
                        new MultiplyOperation(new ScalarLiteral(2), new PixelLiteral(10)),
                        new PixelLiteral(20)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionMultiplicationCases")
    @Tag("TR01")
    @DisplayName("TR01: Multiplication expressions are evaluated correctly")
    void TR01_Multiplication_EvaluatesCorrectly(EvalCase testCase) {
        Fixtures.ASTPair pair = Fixtures.createExpressionEvalPair(testCase.input(), testCase.Expected());
        assertEvaluatedCorrectly(pair);
    }

    // --- TR01: Mixed Operations Evaluation ---

    static Stream<Arguments> MixedOperationsCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "MixedAddAndMultiply",
                        new AddOperation(
                                new MultiplyOperation(
                                        new PixelLiteral(2),
                                        new ScalarLiteral(3)
                                ),
                                new PixelLiteral(4)
                        ),
                        new PixelLiteral(10)
                )),
                Arguments.of(new EvalCase(
                        "MixedSubtractAndMultiply",
                        new SubtractOperation(
                                new MultiplyOperation(
                                        new PercentageLiteral(5),
                                        new ScalarLiteral(2)
                                ),
                                new PercentageLiteral(3)
                        ),
                        new PercentageLiteral(7)
                )),
                Arguments.of(new EvalCase(
                        "MixedAddSubtractAndMultiply",
                        new AddOperation(
                                new SubtractOperation(
                                        new ScalarLiteral(10),
                                        new ScalarLiteral(2)
                                ),
                                new MultiplyOperation(
                                        new ScalarLiteral(2),
                                        new ScalarLiteral(3)
                                )
                        ),
                        new ScalarLiteral(14)
                )),
                Arguments.of(new EvalCase(
                        "MixedAddSubtractAndMultiplyPixels",
                        new AddOperation(
                                new SubtractOperation(
                                        new PixelLiteral(10),
                                        new PixelLiteral(2)
                                ),
                                new MultiplyOperation(
                                        new ScalarLiteral(2),
                                        new PixelLiteral(3)
                                )
                        ),
                        new PixelLiteral(14)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("MixedOperationsCases")
    @Tag("TR01")
    @DisplayName("TR01: Mixed operations (add, subtract, multiply) are evaluated correctly")
    void TR01_MixedOperations_EvaluatesCorrectly(EvalCase testCase) {
        Fixtures.ASTPair pair = Fixtures.createExpressionEvalPair(testCase.input(), testCase.Expected());
        assertEvaluatedCorrectly(pair);
    }

    // --- TR02: If/Else Evaluation ---

    public record EvalConditionalRuleCase(String name, Supplier<Fixtures.ASTPair> build) {
        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    static Stream<Arguments> IfElseCases() {
        return Stream.of(
                Arguments.of(
                        new EvalConditionalRuleCase(
                                "a: If True",
                                () -> Fixtures.createConditionalRulePair(
                                        true,
                                        new ASTNode[]{new PixelLiteral(10)}
                                )
                        )
                ),
                Arguments.of(
                        new EvalConditionalRuleCase(
                                "a: If True with Prefix Suffix",
                                () -> Fixtures.createConditionalRulePair(
                                        true,
                                        new ASTNode[]{new PixelLiteral(10)},
                                        new ASTNode[]{new PixelLiteral(20)},
                                        new ASTNode[]{new PixelLiteral(30)}
                                )
                        )
                ),
                Arguments.of(
                        new EvalConditionalRuleCase(
                                "a: If False",
                                () -> Fixtures.createConditionalRulePair(
                                        false,
                                        new ASTNode[]{new PixelLiteral(10)}
                                )
                        )
                ),
                Arguments.of(
                        new EvalConditionalRuleCase(
                                "a: If False with Prefix Suffix",
                                () -> Fixtures.createConditionalRulePair(
                                        false,
                                        new ASTNode[]{new PixelLiteral(10)},
                                        new ASTNode[]{new PixelLiteral(20)},
                                        new ASTNode[]{new PixelLiteral(30)}
                                )
                        )
                ),
                Arguments.of(
                        new EvalConditionalRuleCase(
                                "b: If-Else True",
                                () -> Fixtures.createConditionalRulePair(
                                        true,
                                        new ASTNode[]{new PixelLiteral(10)},
                                        new ASTNode[]{new PixelLiteral(20)}
                                )
                        )
                ),
                Arguments.of(
                        new EvalConditionalRuleCase(
                                "b: If-Else False",
                                () -> Fixtures.createConditionalRulePair(
                                        false,
                                        new ASTNode[]{new PixelLiteral(10)},
                                        new ASTNode[]{new PixelLiteral(20)}
                                )
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("IfElseCases")
    @Tag("TR02")
    @DisplayName("TR02: If/Else expressions are evaluated correctly")
    void TR02_IfElse_EvaluatesCorrectly(EvalConditionalRuleCase testCase) {
        Fixtures.ASTPair pair = testCase.build().get();
        assertEvaluatedCorrectly(pair);
    }

}
