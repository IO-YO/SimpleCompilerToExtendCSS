package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;

import java.util.Set;

public class ASTScopeRules {
    private static final Set<Class<? extends ASTNode>> scopeNodes = Set.of(
            Stylesheet.class,
            Stylerule.class,
            IfClause.class,
            ElseClause.class
    );

    public static boolean opensScope(ASTNode node) {
        return scopeNodes.contains(node.getClass());
    }
}
