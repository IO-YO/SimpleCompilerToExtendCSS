package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
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
        StyleSheet sheet = new StyleSheet();
        nodeStack.push(sheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        StyleSheet sheet = (StyleSheet) nodeStack.pop();
        ast.setRoot(sheet);
    }

    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        StyleRule rule = new StyleRule();
        if (ctx.getChild(0).getText().startsWith(".")) {
            rule.addChild(new ClassSelector(ctx.getChild(0).getText()));
        }
        else if (ctx.getChild(0).getText().startsWith("#")) {
            rule.addChild(new IdSelector(ctx.getChild(0).getText()));
        }
        else {
            rule.addChild(new TagSelector(ctx.getChild(0).getText()));
        }
        nodeStack.push(rule);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
        StyleRule rule = (StyleRule) nodeStack.pop();
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
    public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        MultiplyOperation multiplyOp = new MultiplyOperation();
        nodeStack.push(multiplyOp);
    }

    @Override
    public void enterAdditiveOperation(ICSSParser.AdditiveOperationContext ctx) {
        if(ctx.PLUS() != null) {
            Operation addOperation = new AddOperation();
            nodeStack.push(addOperation);
        }
        else {
            Operation subtractOperation = new SubtractOperation();
            nodeStack.push(subtractOperation);
        }
    }

    @Override
    public void exitAdditiveOperation(ICSSParser.AdditiveOperationContext ctx) {
        if(ctx.PLUS() != null) {
            Operation addOperation = (AddOperation) nodeStack.pop();
            nodeStack.peek().addChild(addOperation);
        }
        else {
            Operation subtractOperation = (SubtractOperation) nodeStack.pop();
            nodeStack.peek().addChild(subtractOperation);
        }
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