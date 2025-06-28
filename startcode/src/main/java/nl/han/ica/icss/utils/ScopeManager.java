package nl.han.ica.icss.utils;

import java.util.*;

public class ScopeManager<T> implements IScopeManager<T> {

    private final Deque<Map<String, T>> scopes;

    public ScopeManager() {
        scopes = new ArrayDeque<>();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.isEmpty()) throw new IllegalStateException("No active scope to exit.");
        scopes.pop();
    }

    public void declare(String name, T type) {
        Map<String, T> current = scopes.peek();
        if (current == null) throw new IllegalStateException("No active scope to declare variable in.");
        current.put(name, type);
    }

    public T resolve(String name) {
        for (Map<String, T> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
}
