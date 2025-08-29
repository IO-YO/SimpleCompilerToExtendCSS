package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;

import java.util.ArrayList;

public class IfClauseBuilder extends ASTBuilderBase<IfClauseBuilder>{

    private final StyleRuleBuilder parent;
    private final Expression condition;
    protected ElseClauseBuilder elseBuilder;

    public IfClauseBuilder(StyleRuleBuilder parent, Expression condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public ElseClauseBuilder elseClause() {
        this.elseBuilder = new ElseClauseBuilder(this);
        return this.elseBuilder;
    }

    public StyleRuleBuilder endIf() {
        IfClause ifClause = new IfClause(condition, new ArrayList<>(body));
        if (elseBuilder != null) {
            ifClause.elseClause = elseBuilder.build();
        }
        parent.addChild(ifClause);
        return parent;
    }

    public IfClauseBuilder ifClause(Expression condition) {
        return new IfClauseBuilder(this.parent, condition) {
            @Override
            public StyleRuleBuilder endIf() {
                var ifc = new IfClause(condition, new ArrayList<>(this.body));
                if (this.elseBuilder != null) {
                    ifc.elseClause = this.elseBuilder.build();
                }
                IfClauseBuilder.this.body.add(ifc);
                return IfClauseBuilder.this.parent;
            }
        };
    }

}
