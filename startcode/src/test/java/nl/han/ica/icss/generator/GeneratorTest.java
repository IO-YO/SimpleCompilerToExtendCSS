package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static nl.han.ica.icss.ASTBuilder.ASTBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
                .reduce((a, b) -> a + "\n" + b).orElse("");
        if (lines.contentEquals("")) return selector + " {\n}";
        return selector + " {\n" + lines + "\n}";
    }

    @Test
    @Tag("GE01")
    void empty_stylesheet() {
        AST ast = styleSheet(); // no rules
        assertEquals("", generator.generate(ast));
    }

    static Stream<Arguments> selectorCases() {
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
                ),
                Arguments.of(
                        "Multiple Style rules",
                        styleSheet(rule(tag("p")), rule(id("id"))),
                        "p {\n}\n#id {\n}"
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @Tag("GE01")
    @MethodSource("selectorCases")
    void test_Selectors(String name, AST ast, String expected) {
        String actual = generator.generate(ast);
        assertEquals(expected, actual);
    }

    private static final String TAG = "p";
    private static final Function<List<Declaration>, AST> AST_RULE_FIXTURE = decls -> styleSheet(rule(tag(TAG), decls));

    static Stream<Arguments> literalCases() {
        return Stream.of(
                arguments(
                        "height px",
                        AST_RULE_FIXTURE.apply(List.of(decl("height", px(5)))),
                        css(TAG, "height: 5px;")
                ),
                arguments(
                        "width percent",
                        AST_RULE_FIXTURE.apply(List.of(decl("width", percent(50)))),
                        css(TAG, "width: 50%;")
                ),
                arguments(
                        "color hex",
                        AST_RULE_FIXTURE.apply(List.of(decl("color", color("#ff0000")))),
                        css(TAG, "color: #ff0000;")
                ),
                arguments(
                        "background-color hex",
                        AST_RULE_FIXTURE.apply(List.of(decl("background-color", color("#00ff00")))),
                        css(TAG, "background-color: #00ff00;")
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @Tag("GE01-02")
    @MethodSource("literalCases")
    void test_Literals(String name, AST ast, String expected) {
        String actual = generator.generate(ast);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> multiDeclarationCases() {
        return Stream.of(
                arguments(
                        "two declarations (px)",
                        AST_RULE_FIXTURE.apply(List.of(
                                decl("width", px(10)),
                                decl("height", px(5))
                        )),
                        css(TAG,
                                "width: 10px;",
                                "height: 5px;"
                        )
                ),
                arguments(
                        "width % + color",
                        AST_RULE_FIXTURE.apply(List.of(
                                decl("width", percent(80)),
                                decl("color", color("#123abc"))
                        )),
                        css(TAG,
                                "width: 80%;",
                                "color: #123abc;"
                        )
                ),
                arguments(
                        "four declarations (mixed)",
                        AST_RULE_FIXTURE.apply(List.of(
                                decl("width", px(100)),
                                decl("height", percent(50)),
                                decl("color", color("#000000")),
                                decl("background-color", color("#eeeeee"))
                        )),
                        css(TAG,
                                "width: 100px;",
                                "height: 50%;",
                                "color: #000000;",
                                "background-color: #eeeeee;"
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @Tag("GE01-02")
    @MethodSource("multiDeclarationCases")
    void test_MultipleDeclarations(String name, AST ast, String expected) {
        String actual = generator.generate(ast);
        assertEquals(expected, actual);
    }

    @Test
    @Tag("GE02")
    void format_lock() {
        AST ast = styleSheet(
                rule(tag("p"), decl("width", px(1))),
                rule(id("x"), decl("color", color("#000000")))
        );
        String expected = """
                p {
                  width: 1px;
                }
                #x {
                  color: #000000;
                }""";
        assertEquals(expected, generator.generate(ast));
    }
}