package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.Expression;

public class IfClauseBuilder extends ASTBuilderBase<IfClauseBuilder>{

    private final StyleRuleBuilder parent;
    private final Expression condition;

    public IfClauseBuilder(StyleRuleBuilder parent, Expression condition) {
        this.parent = parent;
        this.condition = condition;
    }
}
