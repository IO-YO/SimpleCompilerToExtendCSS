package nl.han.ica.icss.scoping;

import nl.han.ica.icss.ast.*;

import java.util.Set;

public class ASTScopeRules {
    private static final Set<Class<? extends ASTNode>> scopeNodes = Set.of(
            Stylesheet.class,
            StyleRule.class,
            IfClause.class,
            ElseClause.class
    );

    public static boolean isScopingNode(ASTNode node) {
        return scopeNodes.contains(node.getClass());
    }
}
