package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Generator {

    private static final String IDENT = "  ";

    public String generate(AST ast) {
        StringBuilder css = new StringBuilder();
        generateStyleSheet(ast.root.getChildren(), css);
        return css.toString().trim();
    }

    private void generateStyleSheet(List<ASTNode> body, StringBuilder css) {
        for (ASTNode child : body) {
            if (child instanceof StyleRule) {
                generateStyleRule((StyleRule) child, css);
            }
        }
    }

    private void generateStyleRule(StyleRule rule, StringBuilder css) {
        css.append(selectorToString(rule.selectors)).append(" {\n");
        for(ASTNode child : rule.body) {
            if(child instanceof Declaration decl) {
                css.append(IDENT).append(declarationToString(decl)).append("\n");
            }
        }
        css.append("}\n");
    }

    private String selectorToString(@NotNull List<Selector> selectors) {
        Selector selector = selectors.getFirst();
        return switch (selector) {
            case TagSelector tag -> tag.tag;
            case IdSelector id -> "#" + id.id;
            case ClassSelector cls -> "." + cls.cls;
            default -> selector.getNodeLabel();
        };
    }

    private String declarationToString(Declaration decl) {
        return decl.property.name + ": " + literalToString(decl.expression) + ";";
    }

    private String literalToString(Expression lit) {
        return String.valueOf(switch(lit) {
            case PixelLiteral p -> p.value + "px";
            case PercentageLiteral p -> p.value + "%";
            case ColorLiteral c -> c.value;
            case ScalarLiteral s -> s.value;
            default -> lit.getNodeLabel();
        });
    }

}

