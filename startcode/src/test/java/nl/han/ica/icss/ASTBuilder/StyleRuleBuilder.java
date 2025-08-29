package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.StyleRule;

public class StyleRuleBuilder {

    private final StylesheetBuilder parent;
    private final StyleRule rule = new StyleRule();

    public StyleRuleBuilder(StylesheetBuilder parent) {
        this.parent = parent;
    }

    public IfClauseBuilder ifClause(Expression condition) {
        return new IfClauseBuilder(this, condition);
    }

}
