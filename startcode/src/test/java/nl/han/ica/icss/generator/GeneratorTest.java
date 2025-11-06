package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static nl.han.ica.icss.ASTBuilder.ASTBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    Generator generator;

    @BeforeEach
    void setUp() {
        generator = new Generator();
    }

    static String css(String selector, String... body) {
        String indent = "  ";
        String lines = java.util.Arrays.stream(body)
                .map(l -> indent + l)
                .reduce((a,b) -> a + "\n" + b).orElse("");
        if (lines.contentEquals("")) return selector + " {\n}";
        return selector + " {\n" + lines + "\n}";
    }

    static Stream<Arguments> styleRulesTestCases() {
        return Stream.of(
                Arguments.of(
                        "Id Selector",
                        styleSheet(rule(id("menu"))),
                        css("#menu")
                ),
                Arguments.of(
                        "Class Selector",
                        styleSheet(rule(cls("class"))),
                        css(".class")
                ),
                Arguments.of(
                        "Tag Selector",
                        styleSheet(rule(tag("p"))),
                        css("p")
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("styleRulesTestCases")
    void testAll_StyleRules(String name, AST ast, String expected) {
        String actual = generator.generate(ast);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> literalTestCases() {
        Function<List<Declaration>, AST> ast = decl -> styleSheet(rule(tag("p"), decl));

        return Stream.of(
                Arguments.of(
                        "name",
                        ast.apply(List.of(decl("width", px(10)))),
                        css("p", "width: 10px;")
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("literalTestCases")
    void testAll_Literals(String name, AST ast, String expected) {
        String actual = generator.generate(ast);
        assertEquals(expected, actual);
    }
}