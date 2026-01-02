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

    public boolean declareIfAbsent(String name, T value) {
        Map<String, T> current = scopes.peek();
        if (current == null) throw new IllegalStateException("No active scopes.");
        return current.putIfAbsent(name, value) == null;
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

    @Override
    public boolean existsInCurrentScope(String name) {
        Map<String, T> current = scopes.peek();
        return current != null && current.containsKey(name);
    }

    public static final class Scope implements AutoCloseable {
        private final ScopeManager<?> mgr;

        private Scope(ScopeManager<?> mgr) {
            this.mgr = mgr;
        }

        @Override
        public void close() {
            mgr.exitScope();
        }
    }

    public Scope enter() {
        this.enterScope();
        return new Scope(this);
    }
}
