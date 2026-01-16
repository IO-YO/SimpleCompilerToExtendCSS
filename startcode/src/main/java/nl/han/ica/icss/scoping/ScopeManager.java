package nl.han.ica.icss.scoping;

import java.util.*;

/**
 * Manages a stack of nested scopes for name-to-value bindings.
 * <p>
 * Scopes are entered and exited explicitly. Declarations are stored in the
 * current (innermost) scope, while resolution searches outward through
 * enclosing scopes.
 * </p>
 * @param <T> the type of values associated with declared names
 */
public class ScopeManager<T> implements IScopeManager<T> {

    private Deque<Map<String, T>> scopes;

    /**
     * Creates an empty scope manager with no active scopes.
     */
    public ScopeManager() {
        scopes = new ArrayDeque<>();
    }

    /**
     * Enters a new, empty scope.
     */
    @Override
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Exits the current scope.
     *
     * @throws IllegalStateException if no scope is active
     */
    @Override
    public void exitScope() {
        if (scopes.isEmpty()) throw new IllegalStateException("No active scope to exit.");
        scopes.pop();
    }

    /**
     * Declares a name in the current scope, overwriting any existing declaration
     * in that scope.
     *
     * @param name the name to declare
     * @param type the associated value
     * @throws IllegalStateException if no scope is active
     */
    @Override
    public void declare(String name, T type) {
        Map<String, T> current = scopes.peek();
        if (current == null) throw new IllegalStateException("No active scope to declare variable in.");
        current.put(name, type);
    }

    /**
     * Declares a name in the current scope only if it is not already present
     * in that scope.
     *
     * @param name the name to declare
     * @param value the associated value
     * @return true if the declaration was added, false if it already existed
     * @throws IllegalStateException if no scope is active
     */
    @Override
    public boolean declareIfAbsent(String name, T value) {
        Map<String, T> current = scopes.peek();
        if (current == null) throw new IllegalStateException("No active scopes.");
        return current.putIfAbsent(name, value) == null;
    }

    /**
     * Resolves a name by searching from the innermost scope outward.
     *
     * @param name the name to resolve
     * @return the associated value, or null if the name is not declared
     */
    @Override
    public T resolve(String name) {
        for (Map<String, T> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    /**
     * Executes the given work within a newly entered scope, which is
     * automatically exited afterwards.
     *
     * @param work the code to execute inside the new scope
     */
    @Override
    public void inNewScope(Runnable work) {
        try (var ignored = enter()) {
            work.run();
        }
    }

    /**
     * Handle representing an active scope. Closing the handle exits the scope.
     */
    public static final class Scope implements AutoCloseable {
        private final ScopeManager<?> mgr;

        private Scope(ScopeManager<?> mgr) {
            this.mgr = mgr;
        }

        /**
         * Exits the associated scope.
         */
        @Override
        public void close() {
            mgr.exitScope();
        }
    }

    /**
     * Enters a new scope and returns a handle that will exit the scope when closed.
     *
     * @return a scope handle for use with try-with-resources
     */
    public Scope enter() {
        this.enterScope();
        return new Scope(this);
    }
}
