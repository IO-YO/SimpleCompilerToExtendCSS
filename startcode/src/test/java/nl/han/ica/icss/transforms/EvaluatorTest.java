package nl.han.ica.icss.transforms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    private void assertEvaluatedCorrectly(Fixtures.ASTPair test) {
        new Evaluator().apply(test.input());
        assertEquals(test.expected(), test.input());
    }

    // --- TR01: Expression Evaluation ---

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Addition of pixel literals is evaluated correctly")
    void TR01_AddPixels_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_addPixels());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Multiply scalar and percentage is evaluated correctly")
    void TR01_MultiplyScalarAndPercentage_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_multiplyScalarPercentage());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Multiply scalar and pixel is evaluated correctly")
    void TR01_MultiplyScalarAndPixel_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_pixelTimesScalar());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Subtract percentage values is evaluated correctly")
    void TR01_SubtractPercentages_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_subtractPercentages());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Precedence respected in mixed add/multiply expressions")
    void TR01_AddMultiplyPrecedence_EvaluatesCorrectly() {
        assertEvaluatedCorrectly(Fixtures.expressionEval_precedence_pxPlusScalarTimesPx());
    }

    // --- TR02: If/Else Evaluation ---

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with true literal includes its body")
    void TR02_IfTrue_IncludesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_TrueCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with false literal removes its body")
    void TR02_IfFalse_RemovesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_FalseCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with true variable includes its body")
    void TR02_IfVarTrue_IncludesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_VariableAssignment_TrueCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If clause with false variable removes its body")
    void TR02_IfVarFalse_RemovesIfBody() {
        assertEvaluatedCorrectly(Fixtures.ifClause_VariableAssignment_FalseCase());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: If/Else clause with false condition keeps else body")
    void TR02_IfWithElse_ConditionFalse_KeepsElseBody() {
        assertEvaluatedCorrectly(Fixtures.ifWithElse_ifFalse_keepsElse());
    }

    @Test
    @Tag("TR02")
    @DisplayName("TR02: Nested if/else applies both outer true and inner false conditions")
    void TR02_NestedIfElse_OuterTrue_InnerFalse_KeepsInnerElse() {
        assertEvaluatedCorrectly(Fixtures.ifIfElse_outerTrueKeepsInnerIf_innerFalseKeepsElse());
    }

    // --- Shared: Variable replacement ---

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Global variable reference is replaced by literal")
    void TR01_GlobalVarReference_ReplacedWithLiteral() {
        assertEvaluatedCorrectly(Fixtures.variableTransform_globalVar_refToRuleBodyDecl());
    }

    @Test
    @Tag("TR01")
    @DisplayName("TR01: Scoped variable in rule is replaced by literal")
    void TR01_ScopedVarInRule_ReplacedWithLiteral() {
        assertEvaluatedCorrectly(Fixtures.variableTransform_scopedVarInStyleRule_refToLiteral());
    }
}
