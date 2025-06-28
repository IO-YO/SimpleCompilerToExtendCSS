package nl.han.ica.icss.transforms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @Test
    void test_IfClause_TrueCondition() {
        Fixtures.ASTPair test = Fixtures.ifClause_TrueCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_IfClause_FalseCondition() {
        Fixtures.ASTPair test = Fixtures.ifClause_FalseCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_IfClause_VariableAssignment_TrueCondition() {
        Fixtures.ASTPair test = Fixtures.ifClause_VariableAssignment_TrueCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void test_IfClause_VariableAssignment_FalseCondition() {
        Fixtures.ASTPair test = Fixtures.ifClause_VariableAssignment_FalseCase();
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    @Test
    void expression_add_pixels() {
        Fixtures.ASTPair t = Fixtures.addOperation_pixels();
        new Evaluator().apply(t.input());
        assertEquals(t.expected(), t.input());
    }

    @Test
    void expression_multiply_scalar_percentage() {
        Fixtures.ASTPair t = Fixtures.multiply_scalar_percentage();
        new Evaluator().apply(t.input());
        assertEquals(t.expected(), t.input());
    }

    @Test
    void variable_replacement_global() {
        Fixtures.ASTPair t = Fixtures.simpleVariableReplacement();
        new Evaluator().apply(t.input());
        assertEquals(t.expected(), t.input());
    }

    @Test
    void variable_replacement_scoped() {
        Fixtures.ASTPair t = Fixtures.scopedVariable();
        new Evaluator().apply(t.input());
        assertEquals(t.expected(), t.input());
    }
}