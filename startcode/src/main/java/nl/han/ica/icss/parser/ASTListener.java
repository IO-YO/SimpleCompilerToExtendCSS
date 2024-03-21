package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Stack;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private IHANStack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new HANStack<>();
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        ASTNode stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.setRoot((Stylesheet) currentContainer.pop());
    }

    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        ASTNode styleRule = new Stylerule();
        currentContainer.push(styleRule);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
        ASTNode styleRule = currentContainer.pop();
        currentContainer.peek().addChild(styleRule);
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
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
        ASTNode selector = currentContainer.pop();
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode declaration = new Declaration();
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode declaration = currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    @Override
    public void enterProperty(ICSSParser.PropertyContext ctx) {
        ASTNode property = new PropertyName(ctx.getText());
        currentContainer.push(property);
    }

    @Override
    public void exitProperty(ICSSParser.PropertyContext ctx) {
        ASTNode property = currentContainer.pop();
        currentContainer.peek().addChild(property);
    }

    @Override
    public void enterLiteral(ICSSParser.LiteralContext ctx) {
        ASTNode literal;
        if (ctx.PIXELSIZE() != null) {
            literal = new PixelLiteral(ctx.PIXELSIZE().getText());
        } else if (ctx.PERCENTAGE() != null) {
            literal = new PercentageLiteral(ctx.PERCENTAGE().getText());
        } else if (ctx.COLOR() != null) {
            literal = new ColorLiteral(ctx.COLOR().getText());
        } else if (ctx.TRUE() != null) {
            literal = new BoolLiteral(ctx.TRUE().getText());
        } else if (ctx.FALSE() != null) {
            literal = new BoolLiteral(ctx.FALSE().getText());
        } else if (ctx.SCALAR() != null) {
            literal = new ScalarLiteral(ctx.SCALAR().getText());
        } else {
            literal = null;
        }
        currentContainer.push(literal);
    }

    @Override
    public void exitLiteral(ICSSParser.LiteralContext ctx) {
        ASTNode literal = currentContainer.pop();
        currentContainer.peek().addChild(literal);
    }

    // ------------------ START OF LEVEL 1 ------------------


    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode variableAssignment = currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        ASTNode variableReference = new VariableReference(ctx.getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        ASTNode variableReference = currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
    }
}