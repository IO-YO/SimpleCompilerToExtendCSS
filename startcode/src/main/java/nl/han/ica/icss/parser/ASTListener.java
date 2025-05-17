package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;

public class ASTListener extends ICSSBaseListener {

    private AST ast;
    private HANStack<ASTNode> nodeStack;

    public ASTListener() {
        ast = new AST();
        nodeStack = new HANStack<>();
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet sheet = new Stylesheet();
        nodeStack.push(sheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet sheet = (Stylesheet) nodeStack.pop();
        ast.setRoot(sheet);
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment varAss = new VariableAssignment();
        nodeStack.push(varAss);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment varAss = (VariableAssignment) nodeStack.pop();
        nodeStack.peek().addChild(varAss);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration decl = new Declaration();
        nodeStack.push(decl);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration decl = (Declaration) nodeStack.pop();
        nodeStack.peek().addChild(decl);
    }

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {


    }
}