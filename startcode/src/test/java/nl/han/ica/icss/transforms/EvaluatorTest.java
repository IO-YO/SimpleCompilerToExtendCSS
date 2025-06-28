package nl.han.ica.icss.transforms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class EvaluatorTest {

    @Test
    void test_ifClause_TrueCase() {
        Fixtures.ASTPair test = Fixtures.ifClause_TrueCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_ifClause_FalseCase() {
        Fixtures.ASTPair test = Fixtures.ifClause_FalseCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_ifClause_VariableAssignment_TrueCase() {
        Fixtures.ASTPair test = Fixtures.ifClause_VariableAssignment_TrueCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_ifClause_VariableAssignment_FalseCase() {
        Fixtures.ASTPair test = Fixtures.ifClause_VariableAssignment_FalseCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_expressionEval_addPixels() {
        Fixtures.ASTPair test = Fixtures.expressionEval_addPixels();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_expressionEval_multiplyScalarPercentage() {
        Fixtures.ASTPair test = Fixtures.expressionEval_multiplyScalarPercentage();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_expressionEval_subtractPercentages() {
        Fixtures.ASTPair test = Fixtures.expressionEval_subtractPercentages();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_expressionEval_pixelTimesScalar() {
        Fixtures.ASTPair test = Fixtures.expressionEval_pixelTimesScalar();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_expressionEval_precedence_pxPlusScalarTimesPx() {
        Fixtures.ASTPair test = Fixtures.expressionEval_precedence_pxPlusScalarTimesPx();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_variableTransform_globalVar_refToRuleBodyDecl() {
        Fixtures.ASTPair test = Fixtures.variableTransform_globalVar_refToRuleBodyDecl();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_variableTransform_scopedVarInStyleRule_refToLiteral() {
        Fixtures.ASTPair test = Fixtures.variableTransform_scopedVarInStyleRule_refToLiteral();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_ifWithElse_ifFalse_keepsElse() {
        Fixtures.ASTPair test = Fixtures.ifWithElse_ifFalse_keepsElse();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_ifIfElse_outerTrueKeepsInnerIf_innerFalseKeepsElse() {
        Fixtures.ASTPair test = Fixtures.ifIfElse_outerTrueKeepsInnerIf_innerFalseKeepsElse();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }
}
