package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.ElseClause;

import java.util.ArrayList;

public class ElseClauseBuilder extends ASTBuilderBase<ElseClauseBuilder> {

    private final IfClauseBuilder parent;

    public ElseClauseBuilder(IfClauseBuilder parent) {
        this.parent = parent;
    }

    public ElseClause build() {
        return new ElseClause(new ArrayList<>(body));
    }

    public IfClauseBuilder endElse(){
        return parent;
    }

}
