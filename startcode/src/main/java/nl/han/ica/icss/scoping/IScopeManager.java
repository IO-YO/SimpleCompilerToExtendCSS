package nl.han.ica.icss.scoping;

public interface IScopeManager<T> {
    /**
     * Enters a new scope.
     */
    void enterScope();

    /**
     * Exits the current scope.
     */
    void exitScope();

    /**
     * Declares a variable in the current scope.
     *
     * @param name The name of the variable.
     * @param type The type of the variable.
     */
    void declare(String name, T type);

    /**
     * Resolves a variable by its name in the current or any outer scope.
     *
     * @param name The name of the variable to resolve.
     * @return The type of the variable, or null if not found.
     */
    T resolve(String name);

    boolean existsInCurrentScope(String name);
}

