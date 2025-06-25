package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.*;

public class ScopeManager {

    private final Deque<Map<String, ExpressionType>> scopes;

    public ScopeManager() {
        scopes = new ArrayDeque<>();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public void declare(String name, ExpressionType type) {
        assert scopes.peek() != null;
        Objects.requireNonNull(scopes.peek().put(name, type));
    }

    public ExpressionType resolve(String name) {
        for (Map<String, ExpressionType> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
}
