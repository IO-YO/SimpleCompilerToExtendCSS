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

    static Stream<Arguments> styleRulesTestCases() {
        return Stream.of(
                Arguments.of(
                        "Id Selector",
                        styleSheet(rule(id("menu"))),
                        "#menu {\n}"
                ),
                Arguments.of(
                        "Class Selector",
                        styleSheet(rule(cls("class"))),
                        ".class {\n}"
                ),
                Arguments.of(
                        "Tag Selector",
                        styleSheet(rule(tag("p"))),
                        "p {\n}"
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
        /*
        p {
            ...
        }
         */
        Function<Declaration, AST> ASTTemplate = decl -> styleSheet(rule("p"), decl);
        Function<String, String> expectedTemplate = decl -> "p {\n\r" + decl + "\n}";

        return Stream.of(
                Arguments.of(
                        "name",
                        ASTTemplate.apply(decl("width", px(10)),


                                )
                )
        );
    }

    private String indent(int level) {
        return "  ".repeat(level);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("")
    void testAll_Literals(String name, AST ast, String expected) {

    }
}