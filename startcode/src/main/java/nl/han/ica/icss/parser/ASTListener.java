package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

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
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule rule = new Stylerule();
        nodeStack.push(rule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule rule = (Stylerule) nodeStack.pop();
        nodeStack.peek().addChild(rule);

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
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference varRef = new VariableReference(ctx.getText());
        nodeStack.push(varRef);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference varRef = (VariableReference) nodeStack.pop();
        nodeStack.peek().addChild(varRef);
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
    public void enterProperty(ICSSParser.PropertyContext ctx) {
        PropertyName property = new PropertyName(ctx.getText());
        nodeStack.push(property);
    }

    @Override
    public void exitProperty(ICSSParser.PropertyContext ctx) {
        PropertyName property = (PropertyName) nodeStack.pop();
        nodeStack.peek().addChild(property);
    }

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSel = new ClassSelector(ctx.getText());
        nodeStack.push(classSel);
    }

    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSel = (ClassSelector) nodeStack.pop();
        nodeStack.peek().addChild(classSel);
    }

    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        IdSelector idSel = new IdSelector(ctx.getText());
        nodeStack.push(idSel);
    }

    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        IdSelector idSel = (IdSelector) nodeStack.pop();
        nodeStack.peek().addChild(idSel);
    }

    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSel = new TagSelector(ctx.getText());
        nodeStack.push(tagSel);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSel = (TagSelector) nodeStack.pop();
        nodeStack.peek().addChild(tagSel);
    }

    @Override
    public void enterAddOperation(ICSSParser.AddOperationContext ctx) {
        AddOperation addOp = new AddOperation();
        nodeStack.push(addOp);
    }

    @Override
    public void exitAddOperation(ICSSParser.AddOperationContext ctx) {
        AddOperation addOp = (AddOperation) nodeStack.pop();
        nodeStack.peek().addChild(addOp);
    }

    @Override
    public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        MultiplyOperation multiplyOp = new MultiplyOperation();
        nodeStack.push(multiplyOp);
    }

    @Override
    public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        MultiplyOperation multiplyOp = (MultiplyOperation) nodeStack.pop();
        nodeStack.peek().addChild(multiplyOp);
    }

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        BoolLiteral boolLit = new BoolLiteral(ctx.getText());
        nodeStack.push(boolLit);
    }

    @Override
    public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        BoolLiteral boolLit = (BoolLiteral) nodeStack.pop();
        nodeStack.peek().addChild(boolLit);
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ColorLiteral colorLit = new ColorLiteral(ctx.getText());
        nodeStack.push(colorLit);
    }

    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ColorLiteral colorLit = (ColorLiteral) nodeStack.pop();
        nodeStack.peek().addChild(colorLit);
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        PercentageLiteral percentageLit = new PercentageLiteral(ctx.getText());
        nodeStack.push(percentageLit);
    }

    @Override
    public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        PercentageLiteral percentageLit = (PercentageLiteral) nodeStack.pop();
        nodeStack.peek().addChild(percentageLit);
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        PixelLiteral pixelLit = new PixelLiteral(ctx.getText());
        nodeStack.push(pixelLit);
    }

    @Override
    public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        PixelLiteral pixelLit = (PixelLiteral) nodeStack.pop();
        nodeStack.peek().addChild(pixelLit);
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ScalarLiteral scalarLit = new ScalarLiteral(ctx.getText());
        nodeStack.push(scalarLit);
    }

    @Override
    public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ScalarLiteral scalarLit = (ScalarLiteral) nodeStack.pop();
        nodeStack.peek().addChild(scalarLit);
    }

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = new IfClause();
        nodeStack.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = (IfClause) nodeStack.pop();
        nodeStack.peek().addChild(ifClause);
    }

    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        ElseClause elseClause = new ElseClause();
        nodeStack.push(elseClause);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        ElseClause elseClause = (ElseClause) nodeStack.pop();
        nodeStack.peek().addChild(elseClause);
    }
}