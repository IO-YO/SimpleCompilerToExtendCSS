package nl.han.ica.icss.scoping;

import java.util.*;

public class ScopeManager<T> implements IScopeManager<T> {

    private final Deque<Map<String, T>> scopes;

    public ScopeManager() {
        scopes = new ArrayDeque<>();
    }

    @Override
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void exitScope() {
        if (scopes.isEmpty())
            throw new IllegalStateException("No active scope to exit.");

        scopes.pop();
    }

    @Override
    public void declare(String name, T type) {
        Map<String, T> current = scopes.peek();
        if (current == null)
            throw new IllegalStateException("No active scope to declare variable in.");

        current.put(name, type);
    }

    @Override
    public T resolve(String name) {
        for (Map<String, T> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
}
