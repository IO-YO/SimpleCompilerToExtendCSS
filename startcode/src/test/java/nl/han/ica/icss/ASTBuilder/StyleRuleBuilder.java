package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.StyleRule;

public class StyleRuleBuilder extends ASTBuilderBase<StyleRuleBuilder> {

    private final StylesheetBuilder parent;
    private final StyleRule rule = new StyleRule();

    public StyleRuleBuilder(StylesheetBuilder parent, Selector selector) {
        this.parent = parent;
        rule.addChild(selector);
    }

    public IfClauseBuilder ifClause(Expression condition) {
        return new IfClauseBuilder(this, condition);
    }

    public StylesheetBuilder endRule() {
        body.forEach(rule::addChild);
        parent.addRule(rule);
        return parent;
    }

}
