package nl.han.ica.icss.utils;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;

import java.util.HashMap;
import java.util.Map;

public class HANScopeManager<T> implements IScopeManager<T> {

    private final IHANStack<Map<String, T>> scopes;

    public HANScopeManager() {
        this.scopes = new HANStack<>();
    }

    @Override
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void exitScope() {
        scopes.pop();
    }

    @Override
    public void declare(String name, T type) {
        Map<String, T> currentScope = scopes.peek();
        if (currentScope == null) throw new IllegalStateException("No active scope to declare variable in.");
        currentScope.put(name, type);
    }

    @Override
    public T resolve(String name) {
        // Booo! A stack sucks for this...
        return null;
    }
}
