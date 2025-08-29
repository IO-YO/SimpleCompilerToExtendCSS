package nl.han.ica.icss.ASTBuilder;

import nl.han.ica.icss.ast.ElseClause;

public class ElseClauseBuilder {

    private final IfClauseBuilder parent;

    public ElseClauseBuilder(IfClauseBuilder parent) {
        this.parent = parent;
    }

    public ElseClause build() {
        return null;
    }
}
