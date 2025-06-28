package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.scoping.ScopeManager;

import static nl.han.ica.icss.scoping.ASTScopeRules.isScopingNode;

public class Evaluator implements Transform {

    ScopeManager<Literal> scopeManager;

    public Evaluator() {
    }

    @Override
    public void apply(AST ast) {
        scopeManager = new ScopeManager<>();
        ASTNode sheet = ast.root;
        doAwesomeTransformation(sheet);
    }

    private void doAwesomeTransformation(ASTNode node) {
        if(isScopingNode(node)) scopeManager.enterScope();

        node.getChildren().forEach(this::doAwesomeTransformation);
        if(isScopingNode(node)) scopeManager.exitScope();
    }

}
