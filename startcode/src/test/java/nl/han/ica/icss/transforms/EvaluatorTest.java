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
}