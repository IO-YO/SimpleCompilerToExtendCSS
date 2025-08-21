package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static nl.han.ica.icss.ASTBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    public record EvalCase(String name, Expression input, Literal expected) {
    }

    private void assertEvaluatedCorrectly(EvaluatorTestBuilder.ASTPair test) {
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    static Stream<Arguments> ExpressionAdditionCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "Add Pixels",
                        addition(px(1), px(1)),
                        px(2)
                )),
                Arguments.of(new EvalCase(
                        "Add Percentages",
                        addition(percent(10), percent(20)),
                        percent(30)
                )),
                Arguments.of(new EvalCase(
                        "Add Scalars",
                        addition(scalar(10), scalar(20)),
                        scalar(30)
                )),
                Arguments.of(new EvalCase(
                        "Add Multiple Pixels",
                        addition(addition(px(5), px(10)), px(15)),
                        px(30)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionAdditionCases")
    @Tag("TR01")
    @DisplayName("TR01: Addition expressions are evaluated correctly")
    void TR01_Addition_EvaluatesCorrectly(EvalCase testCase) {
        assertEvaluatedCorrectly(
                EvaluatorTestBuilder.build()
                        .input(
                                rule("p",
                                        decl("width", testCase.input())
                                )
                        )
                        .expected(
                                rule("p",
                                        decl("width", testCase.expected())
                                )
                        )
                        .toPair()
        );
    }

    static Stream<Arguments> ExpressionSubtractionCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "Subtract Pixels",
                        subtract(px(10), px(5)),
                        px(5)
                )),
                Arguments.of(new EvalCase(
                        "Subtract Percentages",
                        subtract(percent(50), percent(20)),
                        percent(30)
                )),
                Arguments.of(new EvalCase(
                        "Subtract Scalars",
                        subtract(scalar(100), scalar(105)),
                        scalar(-5)
                )),
                Arguments.of(new EvalCase(
                        "Subtract Multiple Pixels",
                        subtract(subtract(px(50), px(20)), px(10)),
                        px(20)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionSubtractionCases")
    @Tag("TR01")
    @DisplayName("TR01: Subtraction expressions are evaluated correctly")
    void TR01_Subtraction_EvaluatesCorrectly(EvalCase testCase) {
        assertEvaluatedCorrectly(
                EvaluatorTestBuilder.build()
                        .input(
                                rule("p",
                                        decl("width", testCase.input())
                                )
                        )
                        .expected(
                                rule("p",
                                        decl("width", testCase.expected())
                                )
                        )
                        .toPair()
        );
    }

    static Stream<Arguments> ExpressionSubtractAndAdditionCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "Subtract And Add Pixels",
                        addition(subtract(px(20), px(10)), px(5)),
                        px(15)
                )),
                Arguments.of(new EvalCase(
                        "Subtract And Add Percentages",
                        addition(subtract(percent(30), percent(10)), percent(5)),
                        percent(25)
                )),
                Arguments.of(new EvalCase(
                        "Subtract And Add Scalars",
                        addition(subtract(scalar(50), scalar(20)), scalar(10)),
                        scalar(40)
                )),
                Arguments.of(new EvalCase(
                        "Subtract And Add Multiple Pixels",
                        addition(subtract(addition(px(30), px(20)), px(10)), px(5)),
                        px(45)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionSubtractAndAdditionCases")
    @Tag("TR01")
    @DisplayName("TR01: Subtraction and addition expressions are evaluated correctly")
    void TR01_SubtractAndAdd_EvaluatesCorrectly(EvalCase testCase) {
        assertEvaluatedCorrectly(
                EvaluatorTestBuilder.build()
                        .input(
                                rule("p",
                                        decl("width", testCase.input())
                                )
                        )
                        .expected(
                                rule("p",
                                        decl("width", testCase.expected())
                                )
                        )
                        .toPair()
        );
    }

    static Stream<Arguments> ExpressionMultiplicationCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "Multiply Scalar",
                        multiply(scalar(4), scalar(5)),
                        scalar(20)
                )),
                Arguments.of(new EvalCase(
                        "Multiply Multiple Pixels",
                        multiply(multiply(scalar(2), px(3)), scalar(4)),
                        px(24)
                )),
                Arguments.of(new EvalCase(
                        "Multiply Scalar And Percentage",
                        multiply(scalar(4), percent(5)),
                        percent(20)
                )),
                Arguments.of(new EvalCase(
                        "Multiply Percentage And Scalar",
                        multiply(percent(4), scalar(5)),
                        percent(20)
                )),
                Arguments.of(new EvalCase(
                        "Multiply Pixel And Scalar",
                        multiply(px(10), scalar(2)),
                        px(20)
                )),
                Arguments.of(new EvalCase(
                        "Multiply Scalar And Pixel",
                        multiply(scalar(2), px(10)),
                        px(20)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ExpressionMultiplicationCases")
    @Tag("TR01")
    @DisplayName("TR01: Multiplication expressions are evaluated correctly")
    void TR01_Multiplication_EvaluatesCorrectly(EvalCase testCase) {
        assertEvaluatedCorrectly(
                EvaluatorTestBuilder.build()
                        .input(
                                rule("p",
                                        decl("width", testCase.input())
                                )
                        )
                        .expected(
                                rule("p",
                                        decl("width", testCase.expected())
                                )
                        )
                        .toPair()
        );
    }

    static Stream<Arguments> MixedOperationsCases() {
        return Stream.of(
                Arguments.of(new EvalCase(
                        "Mixed Add And Multiply",
                        addition(multiply(px(2), scalar(3)), px(4)),
                        px(10)
                )),
                Arguments.of(new EvalCase(
                        "Mixed Subtract And Multiply",
                        subtract(multiply(percent(5), scalar(2)), percent(3)),
                        percent(7)
                )),
                Arguments.of(new EvalCase(
                        "Mixed Add Subtract And Multiply",
                        addition(
                                subtract(scalar(10), scalar(2)),
                                multiply(scalar(2), scalar(3))
                        ),
                        scalar(14)
                )),
                Arguments.of(new EvalCase(
                        "Mixed Add Subtract And Multiply Pixels",
                        addition(
                                subtract(px(10), px(2)),
                                multiply(scalar(2), px(3))
                        ),
                        px(14)
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("MixedOperationsCases")
    @Tag("TR01")
    @DisplayName("TR01: Mixed operations (add, subtract, multiply) are evaluated correctly")
    void TR01_MixedOperations_EvaluatesCorrectly(EvalCase testCase) {
        assertEvaluatedCorrectly(
                EvaluatorTestBuilder.build()
                        .input(
                                rule("p",
                                        decl("width", testCase.input())
                                )
                        )
                        .expected(
                                rule("p",
                                        decl("width", testCase.expected())
                                )
                        )
                        .toPair()
        );
    }

    public record EvalConditionalRuleCase(String name, Supplier<EvaluatorTestBuilder.ASTPair> build) {
        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    static Stream<Arguments> IfElseCases() {
        return Stream.of(
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(True)",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        rule("p",
                                                ifClause(bool(true), decl("width", px(10)))))
                                .expected(
                                        rule("p",
                                                decl("width", px(10))))
                                .toPair()
                )),
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(False)",
                        () -> EvaluatorTestBuilder.build()
                                .input(rule("p", ifClause(bool(false), decl("width", px(10)))))
                                .expected(rule("p"))
                                .toPair()
                )),
                Arguments.of(new EvalConditionalRuleCase(
                        "b: If(true) no Else",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        rule("p",
                                                ifElseClause(
                                                        bool(true),
                                                        new ASTNode[]{decl("width", px(10))},
                                                        new ASTNode[]{decl("width", px(20))}
                                                )
                                        )
                                )
                                .expected(rule("p", decl("width", px(10))))
                                .toPair()
                )),
                Arguments.of(new EvalConditionalRuleCase(
                        "b: If(false) then Else",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        rule("p",
                                                ifElseClause(
                                                        bool(false),
                                                        decl("width", px(10)),
                                                        decl("width", px(20)))))
                                .expected(
                                        rule("p",
                                                decl("width", px(20))))
                                .toPair()
                )),
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(True) with Prefix and Suffix",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        rule("p",
                                                decl("margin", px(20)),
                                                ifClause(
                                                        bool(true),
                                                        decl("width", px(10))),
                                                decl("padding", px(30))
                                        )
                                )
                                .expected(
                                        rule("p",
                                                decl("margin", px(20)),
                                                decl("width", px(10)),
                                                decl("padding", px(30))
                                        )
                                )
                                .toPair()
                )),
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(False) with Prefix and Suffix",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        rule("p",
                                                decl("margin", px(20)),
                                                ifClause(
                                                        bool(false),
                                                        decl("width", px(10))),
                                                decl("padding", px(30))
                                        )
                                )
                                .expected(
                                        rule("p",
                                                decl("margin", px(20)),
                                                decl("padding", px(30))
                                        )
                                )
                                .toPair()
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("IfElseCases")
    @Tag("TR02")
    @DisplayName("TR02: If/Else expressions are evaluated correctly")
    void TR02_IfElse_EvaluatesCorrectly(EvalConditionalRuleCase testCase) {
        assertEvaluatedCorrectly(testCase.build().get());
    }

}
