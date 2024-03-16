package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.IdSelector;

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
		super.enterStylesheet(ctx);
		ASTNode stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		super.exitStylesheet(ctx);
		ast.setRoot((Stylesheet) currentContainer.pop());
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		super.enterStylerule(ctx);
		ASTNode stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		super.exitStylerule(ctx);
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterIdentificator(ICSSParser.IdentificatorContext ctx) {
		super.enterIdentificator(ctx);
		ASTNode idSelector = new IdSelector(ctx.getText());
		currentContainer.push(idSelector);
	}

	@Override
	public void exitIdentificator(ICSSParser.IdentificatorContext ctx) {
		super.exitIdentificator(ctx);
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		super.enterDeclaration(ctx);
		ASTNode declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		super.exitDeclaration(ctx);
		currentContainer.peek().addChild(currentContainer.pop());
	}
}