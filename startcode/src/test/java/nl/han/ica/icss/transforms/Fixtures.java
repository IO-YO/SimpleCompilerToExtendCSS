package nl.han.ica.icss.transforms;

import static nl.han.ica.icss.ASTBuilder.*;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import org.jetbrains.annotations.NotNull;

public class Fixtures {

    public record ASTPair(AST input, AST expected) {
    }

    public static ASTPair createExpressionEvalPair(Expression expression, Literal expectedLiteral) {
        AST input = stylesheet(
                rule("p",
                        decl("width", expression)
                )
        );

        AST expected = stylesheet(
                rule("p",
                        decl("width", expectedLiteral)
                )
        );

        return new ASTPair(input, expected);
    }

    public static ASTPair createConditionalRulePair(
            @NotNull Expression condition,
            @NotNull ASTNode[] ifBody,
            @NotNull ASTNode[] elseBody,
            @NotNull ASTNode[] expectedBody,
            @NotNull ASTNode[] prefix,
            @NotNull ASTNode[] suffix
    ) {

        ASTNode clauseNode = (elseBody.length == 0)
                ? ifClause(condition, ifBody)
                : ifElseClause(condition, ifBody, elseBody);

        ASTNode[] ruleBody = RuleBody
                .start(prefix)
                .then(clauseNode)
                .thenAll(suffix)
                .toArray();

        AST input = stylesheet(rule("p", ruleBody));
        AST expected = stylesheet(rule("p", cloneBody(expectedBody)));

        return new ASTPair(input, expected);
    }

    public static ASTPair createConditionalRulePair(
            boolean condition,
            @NotNull ASTNode[] ifBody,
            @NotNull ASTNode[] elseBody,
            @NotNull ASTNode[] prefix,
            @NotNull ASTNode[] suffix
    ) {

        ASTNode[] pickedBody = condition ? ifBody : elseBody;

        ASTNode[] expectedBody = RuleBody
                .start(prefix)
                .thenAll(pickedBody)
                .thenAll(suffix)
                .toArray();

        return createConditionalRulePair(
                new BoolLiteral(condition),
                ifBody,
                elseBody,
                expectedBody,
                prefix,
                suffix
        );
    }

    public static ASTPair createConditionalRulePair(
            boolean condition,
            @NotNull ASTNode[] ifBody,
            @NotNull ASTNode[] elseBody
    ) {

        return createConditionalRulePair(
                condition,
                ifBody,
                elseBody,
                new ASTNode[0],
                new ASTNode[0]
        );
    }

    public static ASTPair createConditionalRulePair(
            boolean condition,
            @NotNull ASTNode[] ifBody
    ) {

        return createConditionalRulePair(
                condition,
                ifBody,
                new ASTNode[0],
                new ASTNode[0],
                new ASTNode[0]
        );
    }

    public static ASTPair createConditionalRulePair(
            boolean condition,
            @NotNull ASTNode[] ifBody,
            @NotNull ASTNode[] prefix,
            @NotNull ASTNode[] suffix
    ) {

        return createConditionalRulePair(
                condition,
                ifBody,
                new ASTNode[0],
                prefix,
                suffix
        );
    }

    private static ASTNode[] cloneBody(ASTNode[] body) {
        if (body == null || body.length == 0) return new ASTNode[0];
        ASTNode[] out = new ASTNode[body.length];
        for (int i = 0; i < body.length; i++)
            out[i] = cloneNode(body[i]);
        return out;
    }

    private static ASTNode cloneNode(ASTNode node) {
        if (node instanceof Declaration d) {
            String prop = d.property.name;
            if (d.expression instanceof PixelLiteral p)
                return decl(prop, new PixelLiteral(p.value));
            if (d.expression instanceof PercentageLiteral p)
                return decl(prop, new PercentageLiteral(p.value));
            if (d.expression instanceof ScalarLiteral s)
                return decl(prop, new ScalarLiteral(s.value));
            if (d.expression instanceof BoolLiteral b)
                return decl(prop, new BoolLiteral(b.value));
            if (d.expression instanceof ColorLiteral c)
                return decl(prop, new ColorLiteral(c.value));
        }
        return node;
    }

    private static final class RuleBody {
        private final java.util.ArrayList<ASTNode> nodes = new java.util.ArrayList<>();

        static RuleBody start(ASTNode[] prefix) {
            RuleBody rb = new RuleBody();
            if (prefix != null) java.util.Collections.addAll(rb.nodes, prefix);
            return rb;
        }

        RuleBody then(ASTNode node) {
            nodes.add(node);
            return this;
        }

        RuleBody thenAll(ASTNode[] more) {
            if (more != null) java.util.Collections.addAll(nodes, more);
            return this;
        }

        ASTNode[] toArray() {
            return nodes.toArray(new ASTNode[0]);
        }
    }
}
