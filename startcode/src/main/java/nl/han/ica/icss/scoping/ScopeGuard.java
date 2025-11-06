package nl.han.ica.icss.scoping;

public final class ScopeGuard implements AutoCloseable {

    private final ScopeManager<?> sm;

    public ScopeGuard(ScopeManager<?> sm) {
        this.sm = sm;
        sm.enterScope();
    }
    @Override
    public void close() {
        sm.exitScope();
    }
}
