package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ASTBuilder.StylesheetBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static nl.han.ica.icss.ASTBuilder.ASTBuilderBase.*;

public class EvaluatorIfElseTest {

    public record EvalConditionalRuleCase(String name, Supplier<EvaluatorTestBuilder.ASTPair> build) {
        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    static Stream<Arguments> IfElseCases() {
        return Stream.of(
                // a: If(True)
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(True)",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .ifClause(bool(true))
                                                .decl("width", px(10))
                                                .endIf()
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("width", px(10))
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                )),

                // a: If(False)
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(False)",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .ifClause(bool(false))
                                                .decl("width", px(10))
                                                .endIf()
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                )),

                // b: If(true) with Else (else ignored)
                Arguments.of(new EvalConditionalRuleCase(
                        "b: If(true) no Else",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .ifClause(bool(true))
                                                .decl("width", px(10))
                                                .elseClause()
                                                .decl("width", px(20))
                                                .endElse()
                                                .endIf()
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("width", px(10))
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                )),

                // b: If(false) then Else
                Arguments.of(new EvalConditionalRuleCase(
                        "b: If(false) then Else",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .ifClause(bool(false))
                                                .decl("width", px(10))
                                                .elseClause()
                                                .decl("width", px(20))
                                                .endElse()
                                                .endIf()
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("width", px(20))
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                )),

                // a: If(True) with Prefix and Suffix
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(True) with Prefix and Suffix",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("margin", px(20))
                                                .ifClause(bool(true))
                                                .decl("width", px(10))
                                                .endIf()
                                                .decl("padding", px(30))
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("margin", px(20))
                                                .decl("width", px(10))
                                                .decl("padding", px(30))
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                )),

                // a: If(False) with Prefix and Suffix
                Arguments.of(new EvalConditionalRuleCase(
                        "a: If(False) with Prefix and Suffix",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("margin", px(20))
                                                .ifClause(bool(false))
                                                .decl("width", px(10))
                                                .endIf()
                                                .decl("padding", px(30))
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .decl("margin", px(20))
                                                .decl("padding", px(30))
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                )),

                // c: Nested If inside body (kept intact by TR02 in your current transformer)
                Arguments.of(new EvalConditionalRuleCase(
                        "c: Nested If inside body",
                        () -> EvaluatorTestBuilder.build()
                                .input(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .ifClause(bool(true))
                                                .decl("width", percent(10))
                                                // nested if
                                                .ifClause(bool(false))
                                                .decl("width", percent(200))
                                                .elseClause()
                                                .decl("width", percent(3000))
                                                .endElse()
                                                .endIf()
                                                .endIf()
                                                .endRule()
                                                .build()
                                )
                                .expected(
                                        StylesheetBuilder.begin()
                                                .rule(tag("p"))
                                                .ifClause(bool(true))
                                                .decl("width", percent(10))
                                                .ifClause(bool(false))
                                                .decl("width", percent(200))
                                                .elseClause()
                                                .decl("width", percent(3000))
                                                .endElse()
                                                .endIf()
                                                .endIf()
                                                .endRule()
                                                .build()
                                )
                                .toPair()
                ))
        );
    }
}
