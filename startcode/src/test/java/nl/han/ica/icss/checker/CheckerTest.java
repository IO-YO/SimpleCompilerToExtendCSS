package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
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
    @Tag("CH03")
    @DisplayName("CH03: Color Operation Checking")
    class CH03_ColorOperationCheck {

    }


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @Tag("CH04")
    @DisplayName("CH04: Property type checking")
    class CH04_Properties {

        record Case(String property, String valueKind, Expression value, boolean valid) {
        }

        Stream<Case> cases() {
            return Stream.of(
                    new Case("width", "PIXEL", px(10), true),
                    new Case("width", "PERCENTAGE", percent(10), true),
                    new Case("width", "SCALAR", scalar(10), false),
                    new Case("width", "COLOR", color("#ff0"), false),

                    new Case("height", "PIXEL", px(10), true),
                    new Case("height", "PERCENTAGE", percent(10), true),
                    new Case("height", "SCALAR", scalar(10), false),

                    new Case("color", "COLOR", color("#00ff00"), true),
                    new Case("color", "PIXEL", px(10), false),
                    new Case("background-color", "COLOR", color("#abcdef"), true),
                    new Case("background-color", "PERCENTAGE", percent(10), false)
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

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @Tag("CH05")
    @DisplayName("CH05: If-condition must be boolean")
    class CH05_IfCondition {

        record Cond(String kind, Expression expr, boolean valid) {
        }

        Stream<Cond> conditions() {
            return Stream.of(
                    new Cond("BOOL", bool(true), true),
                    new Cond("PIXEL", px(1), false),
                    new Cond("SCALAR", scalar(1), false),
                    new Cond("COLOR", color("#010203"), false)
            );
        }

        @ParameterizedTest
        @MethodSource("conditions")
        void literal_condition(Cond c) {
            AST ast = checkFixture(styleSheet(
                            rule("p",
                                    ifClause(
                                            c.expr,
                                            decl("width", px(1))
                                    )
                            )
                    )
            );

            if (c.valid) assertNoErrors(ast);
            else assertSingleError(ast, "If-condition");
        }

        @ParameterizedTest
        @MethodSource("conditions")
        void variable_condition(Cond c) {
            AST ast = checkFixture(styleSheet(
                    varAssignment("Flag", c.expr),
                    rule("p",
                            ifClause(
                                    varRef("Flag"),
                                    decl("width",
                                            px(1))))));

            if (c.valid) assertNoErrors(ast);
            else assertSingleError(ast, "If-condition");
        }
    }

    @Nested
    @DisplayName("CH06: Scoping")
    class CH06_Scoping {

        static Stream<Arguments> scenarios() {
            return Stream.of(
                    Arguments.of("outer var used inside if", true,
                            styleSheet(
                                    varAssignment("Test", px(10)),
                                    rule("p",
                                            ifClause(
                                                    bool(true),
                                                    decl("width", varRef("Test"))))
                            )
                    ),
                    Arguments.of("if var leaks outside", false,
                            styleSheet(
                                    rule("p",
                                            ifClause(
                                                    bool(true),
                                                    varAssignment("X", px(1))),
                                            decl("width", varRef("X")))
                            )
                    ),
                    Arguments.of("else var leaks outside", false,
                            styleSheet(
                                    rule("p", ifElseClause(
                                                    bool(false), null,
                                                    varAssignment("Y", px(2))),
                                            decl("width", varRef("Y")))
                            )
                    )
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("scenarios")
        void scope_cases(String name, boolean valid, AST fixture) {
            AST ast = checkFixture(fixture);
            if (valid) assertNoErrors(ast);
            else assertSingleError(ast, "Unknown variable");
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("CH02: Arithmetic typing")
    class CH02_Operations {

        enum OpName {ADD, SUB, MUL}

        enum LitName {PIXEL, PERCENTAGE, SCALAR, COLOR, BOOL}

        private Expression lit(LitName lit) {
            return switch (lit) {
                case PIXEL -> px(10);
                case PERCENTAGE -> percent(10);
                case SCALAR -> scalar(10);
                case COLOR -> color("#112233");
                case BOOL -> bool(true);
            };
        }

        private Expression make(OpName op, Expression l, Expression r) {
            return switch (op) {
                case ADD -> addition(l, r);
                case SUB -> subtract(l, r);
                case MUL -> multiply(l, r);
            };
        }

        Stream<Arguments> subCases() {
            return Stream.of(
                    Arguments.of(OpName.SUB, LitName.PIXEL, LitName.PIXEL, true),
                    Arguments.of(OpName.SUB, LitName.PERCENTAGE, LitName.PERCENTAGE, true),
                    Arguments.of(OpName.SUB, LitName.SCALAR, LitName.SCALAR, true),

                    Arguments.of(OpName.SUB, LitName.PIXEL, LitName.PERCENTAGE, false),
                    Arguments.of(OpName.SUB, LitName.PIXEL, LitName.SCALAR, false),
                    Arguments.of(OpName.SUB, LitName.PERCENTAGE, LitName.SCALAR, false),

                    Arguments.of(OpName.SUB, LitName.COLOR, LitName.COLOR, false),
                    Arguments.of(OpName.SUB, LitName.COLOR, LitName.PIXEL, false),
                    Arguments.of(OpName.SUB, LitName.PIXEL, LitName.COLOR, false),
                    Arguments.of(OpName.SUB, LitName.BOOL, LitName.PIXEL, false),
                    Arguments.of(OpName.SUB, LitName.PIXEL, LitName.BOOL, false),
                    Arguments.of(OpName.SUB, LitName.BOOL, LitName.BOOL, false)
            );
        }

        @ParameterizedTest(name = "{0}: {1}, {2} = {3}")
        @MethodSource("subCases")
        void SubtractOpMatrix(OpName op, LitName left, LitName right, boolean expectedValid) {
            Expression node = make(op, lit(left), lit(right));
            AST ast = checkFixture(styleSheet(varAssignment("Test", node)));

            if (expectedValid) {
                assertNoErrors(ast);
            } else {
                assertSingleError(ast, "Sub");
            }
        }

        Stream<Arguments> addCases() {
            return Stream.of(
                    Arguments.of(OpName.ADD, LitName.PIXEL, LitName.PIXEL, true),
                    Arguments.of(OpName.ADD, LitName.PERCENTAGE, LitName.PERCENTAGE, true),
                    Arguments.of(OpName.ADD, LitName.SCALAR, LitName.SCALAR, true),

                    Arguments.of(OpName.ADD, LitName.PIXEL, LitName.PERCENTAGE, false),
                    Arguments.of(OpName.ADD, LitName.PIXEL, LitName.SCALAR, false),
                    Arguments.of(OpName.ADD, LitName.PERCENTAGE, LitName.SCALAR, false),

                    Arguments.of(OpName.ADD, LitName.COLOR, LitName.PIXEL, false),
                    Arguments.of(OpName.ADD, LitName.PIXEL, LitName.COLOR, false),
                    Arguments.of(OpName.ADD, LitName.COLOR, LitName.COLOR, false),
                    Arguments.of(OpName.ADD, LitName.BOOL, LitName.PIXEL, false),
                    Arguments.of(OpName.ADD, LitName.PIXEL, LitName.BOOL, false),
                    Arguments.of(OpName.ADD, LitName.BOOL, LitName.BOOL, false)
            );
        }

        @ParameterizedTest(name = "{0}: {1}, {2} = {3}")
        @MethodSource("addCases")
        void additionOpMatrix(OpName op, LitName left, LitName right, boolean expectedValid) {
            Expression node = make(op, lit(left), lit(right));
            AST ast = checkFixture(styleSheet(varAssignment("Test", node)));

            if (expectedValid) {
                assertNoErrors(ast);
            } else {
                assertSingleError(ast, "Add");
            }
        }

        Stream<Arguments> multiplyCases() {
            return Stream.of(
                    Arguments.of(OpName.MUL, LitName.SCALAR, LitName.PIXEL, true),
                    Arguments.of(OpName.MUL, LitName.PIXEL, LitName.SCALAR, true),
                    Arguments.of(OpName.MUL, LitName.SCALAR, LitName.PERCENTAGE, true),
                    Arguments.of(OpName.MUL, LitName.PERCENTAGE, LitName.SCALAR, true),
                    Arguments.of(OpName.MUL, LitName.SCALAR, LitName.SCALAR, true),

                    Arguments.of(OpName.MUL, LitName.PIXEL, LitName.PIXEL, false),
                    Arguments.of(OpName.MUL, LitName.PERCENTAGE, LitName.PERCENTAGE, false),
                    Arguments.of(OpName.MUL, LitName.PIXEL, LitName.PERCENTAGE, false),
                    Arguments.of(OpName.MUL, LitName.PERCENTAGE, LitName.PIXEL, false),

                    Arguments.of(OpName.MUL, LitName.COLOR, LitName.PIXEL, false),
                    Arguments.of(OpName.MUL, LitName.PIXEL, LitName.COLOR, false),
                    Arguments.of(OpName.MUL, LitName.COLOR, LitName.SCALAR, false),
                    Arguments.of(OpName.MUL, LitName.SCALAR, LitName.COLOR, false),
                    Arguments.of(OpName.MUL, LitName.COLOR, LitName.COLOR, false),
                    Arguments.of(OpName.MUL, LitName.BOOL, LitName.SCALAR, false),
                    Arguments.of(OpName.MUL, LitName.SCALAR, LitName.BOOL, false),
                    Arguments.of(OpName.MUL, LitName.BOOL, LitName.BOOL, false)
            );
        }

        @ParameterizedTest(name = "{0}: {1}, {2} = {3}")
        @MethodSource("multiplyCases")
        void multiplyMatrix(OpName op, LitName left, LitName right, boolean expectedValid) {
            Expression node = make(op, lit(left), lit(right));
            AST ast = checkFixture(styleSheet(varAssignment("Test", node)));

            if (expectedValid) {
                assertNoErrors(ast);
            } else {
                assertSingleError(ast, "Multiply");
            }
        }

    }

}