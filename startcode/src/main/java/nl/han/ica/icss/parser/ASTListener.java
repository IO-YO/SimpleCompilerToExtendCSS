package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.Stack;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private Stack<ASTNode> currentContainer; // TO DO: Replace with IHANStack

    public ASTListener() {
        ast = new AST();
        currentContainer = new Stack<>(); // TO DO: Replace with HANStack
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
// //        super.enterStylesheet(ctx);
        ASTNode stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
// //        super.exitStylesheet(ctx);
        ast.setRoot((Stylesheet) currentContainer.pop());
    }

    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
//        super.enterStyleRule(ctx);
        ASTNode styleRule = new Stylerule();
        currentContainer.push(styleRule);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
//        super.exitStyleRule(ctx);
        currentContainer.peek().addChild(currentContainer.pop());
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
//        super.enterSelector(ctx);
        ASTNode selector;
        if (ctx.ID_IDENT() != null) {
            selector = new IdSelector(ctx.ID_IDENT().getText());
        } else if (ctx.CLASS_IDENT() != null) {
            selector = new ClassSelector(ctx.CLASS_IDENT().getText());
        } else if (ctx.LOWER_IDENT() != null) {
            selector = new TagSelector(ctx.LOWER_IDENT().getText());
        } else {
            selector = null;
        }
        currentContainer.push(selector);
    }

    @Override
    public void exitSelector(ICSSParser.SelectorContext ctx) {
//        super.exitSelector(ctx);
        currentContainer.peek().addChild(currentContainer.pop());
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
//         super.enterDeclaration(ctx);
        ASTNode declaration = new Declaration();
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
//         super.exitDeclaration(ctx);
        currentContainer.peek().addChild(currentContainer.pop());
    }

    @Override
    public void enterProperty(ICSSParser.PropertyContext ctx) {
//         super.enterProperty(ctx);
        ASTNode property = new PropertyName(ctx.getText());
        currentContainer.push(property);
    }

    @Override
    public void exitProperty(ICSSParser.PropertyContext ctx) {
//         super.exitProperty(ctx);
        currentContainer.peek().addChild(currentContainer.pop());
    }
}