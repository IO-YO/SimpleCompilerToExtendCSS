package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;

public class ASTListener extends ICSSBaseListener {

    private AST ast;

    public ASTListener() {
        ast = new AST();
    }

    public AST getAST() {
        return ast;
    }
}