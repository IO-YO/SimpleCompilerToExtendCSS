package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.StyleRule;
import nl.han.ica.icss.ast.Stylesheet;


public class StylesheetBuilder extends ASTBuilderBase<StylesheetBuilder> {

    private final Stylesheet stylesheet = new Stylesheet();

    public static StylesheetBuilder begin() {
        return new StylesheetBuilder();
    }

    public StyleRuleBuilder rule(Selector selector) {
        return new StyleRuleBuilder(this, selector);
    }

    public void addRule(StyleRule rule) {
        stylesheet.addChild(rule);
    }

    public AST build() {
        body.forEach(stylesheet::addChild);
        return new AST(stylesheet);
    }

}
