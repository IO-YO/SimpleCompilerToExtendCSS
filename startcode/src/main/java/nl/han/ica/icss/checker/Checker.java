package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.scoping.ScopeManager;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static nl.han.ica.icss.ast.types.ExpressionType.*;

public class Checker {

    /**
     * Manages the scoping and resolution of variable declarations and types
     * within the context of the {@link ExpressionType} system.
     *
     * @see ScopeManager
     * @see ExpressionType
     */
    private ScopeManager<ExpressionType> scopeManager;

    /**
     * A predefined set of {@link ExpressionType}s representing non-numeric types
     * used during type validation in the {@code Checker} class.
     */
    private final EnumSet<ExpressionType> nonNumericTypes = EnumSet.of(BOOL, COLOR);

    /**
     * Performs a validation check on the provided Abstract Syntax Tree (AST).
     *
     * @param ast the Abstract Syntax Tree to validate
     */
    public void check(AST ast) {
        // TODO: Make checker stateless; it's not because of this
        scopeManager = new ScopeManager<>();
        scopeManager.inNewScope(() -> checkBody(ast.root.getChildren()));
    }

    /**
     * Traverses and checks a list of AST nodes by dispatching each node
     * to its corresponding handler.
     *
     * @param body the list of AST nodes to check
     */
    private void checkBody(List<ASTNode> body) {
        for (ASTNode child : body) {
            switch (child) {
                case VariableAssignment va -> handleVariableAssignment(va);
                case Declaration decl -> handleDeclaration(decl);
                case IfClause ifc -> handleIfClause(ifc);
                case StyleRule rule -> handleStyleRule(rule);
                default -> {} // skip
            }
        }
    }

    /**
     * Processes a {@link StyleRule} by checking its body in a new scope.
     *
     * @param rule the style rule to process
     */
    private void handleStyleRule(StyleRule rule) {
        scopeManager.inNewScope(() -> checkBody(rule.getChildren()));
    }

    /**
     * Processes an {@link IfClause} by validating its condition and checking
     * the bodies of the if- and else-branches in separate scopes.
     *
     * @param ifc the if-clause to process
     */
    private void handleIfClause(IfClause ifc) {
        checkIfCondition(ifc);

        scopeManager.inNewScope(() -> checkBody(ifc.body));

        if (ifc.elseClause != null) {
            scopeManager.inNewScope(() -> checkBody(ifc.elseClause.getChildren()));
        }
    }

    /**
     * Validates the condition of an {@link IfClause}.
     *
     * <p>The condition must be present and resolve to {@link ExpressionType#BOOL}.
     * Violations are recorded as errors on the if-clause.</p>
     *
     * @param ifc the if-clause whose condition is checked
     */
    private void checkIfCondition(IfClause ifc) {
        if (ifc.conditionalExpression == null) {
            ifc.setError("If-condition is missing");
            return;
        }
        ExpressionType type = resolveExpressionType(ifc.conditionalExpression);
        if (type != BOOL) {
            ifc.setError("If-condition must be a boolean, but got: " + type);
        }
    }

    /**
     * Handles a variable assignment by resolving the expression type and declaring
     * the variable in the current scope.
     *
     * <p>If the variable name is already declared in the same scope, an error is
     * recorded on the assignment.</p>
     *
     * @param varAss the variable assignment to process
     */
    private void handleVariableAssignment(VariableAssignment varAss) {
        ExpressionType type = resolveExpressionType(varAss.expression);
        String varName = varAss.name.name;
        if (!scopeManager.declareIfAbsent(varName, type)) {
            varAss.setError("Variable '" + varName + "' redeclared in the same scope");
        }
    }

    /**
     * Resolves the {@link ExpressionType} of an expression by delegating to
     * expression-specific resolution logic.
     *
     * <p>Unsupported expression types record an error on the expression node and
     * result in {@link ExpressionType#UNDEFINED}.</p>
     *
     * @param expr the expression to resolve
     * @return the resolved {@link ExpressionType}, or {@link ExpressionType#UNDEFINED} if unsupported
     */
    private ExpressionType resolveExpressionType(@NotNull Expression expr) {
        return switch (expr) {
            case VariableReference ref -> resolveVariableRef(ref);
            case Operation op -> resolveOperationType(op);
            case Literal lit -> resolveLiteralType(lit);
            default -> {
                expr.setError("Unsupported expression: " + expr.getClass().getSimpleName());
                yield UNDEFINED;
            }
        };
    }

    /**
     * Resolves the {@link ExpressionType} corresponding to a literal.
     *
     * <p>If the literal type is not recognized, an error is recorded on the literal
     * node and {@link ExpressionType#UNDEFINED} is returned.</p>
     *
     * @param lit the literal to resolve
     * @return the resolved {@link ExpressionType}, or {@link ExpressionType#UNDEFINED} if unknown
     */
    private ExpressionType resolveLiteralType(Literal lit) {
        return switch (lit) {
            case PixelLiteral _ -> PIXEL;
            case ScalarLiteral _ -> SCALAR;
            case BoolLiteral _ -> BOOL;
            case PercentageLiteral _ -> PERCENTAGE;
            case ColorLiteral _ -> COLOR;
            default -> {
                lit.setError("Unknown literal type: " + lit.getClass().getSimpleName());
                yield UNDEFINED;
            }
        };
    }

    /**
     * Resolves the resulting {@link ExpressionType} of an operation by first resolving
     * its operand types and then delegating to the appropriate operation-specific rule.
     *
     * <p>If either operand resolves to {@link ExpressionType#UNDEFINED}, this method
     * returns {@code UNDEFINED} immediately. Unsupported operations record an error
     * on the operation node.</p>
     *
     * @param op the operation to resolve
     * @return the resulting {@link ExpressionType}, or {@link ExpressionType#UNDEFINED} if invalid
     */
    private ExpressionType resolveOperationType(Operation op) {
        var left = resolveExpressionType(op.lhs);
        var right = resolveExpressionType(op.rhs);

        if (left == UNDEFINED || right == UNDEFINED) return UNDEFINED;

        return switch (op) {
            case SubtractOperation _, AddOperation _ -> resolveAdditiveOperation(op, left, right);
            case MultiplyOperation _ -> resolveMultiplyOperation(op, left, right);
            default -> {
                op.setError("Unknown operation: " + op.getClass().getSimpleName());
                yield UNDEFINED;
            }
        };
    }

    /**
     * Resolves the result type of an additive operation.
     *
     * <p>Addition and subtraction are only valid when both operands have the same
     * numeric type. If valid, that type is returned; otherwise an error is recorded
     * on the operation and {@link ExpressionType#UNDEFINED} is returned.</p>
     *
     * @param op    the additive operation
     * @param left  the left operand type
     * @param right the right operand type
     * @return the resulting type, or {@link ExpressionType#UNDEFINED} if invalid
     */
    private ExpressionType resolveAdditiveOperation(Operation op, ExpressionType left, ExpressionType right) {
        boolean areNotSameType = left != right;
        if (areNotSameType || nonNumericTypes.contains(left)) {
            op.setError("Invalid operands for " + op.getNodeLabel() + ": " + left + " and " + right);
            return UNDEFINED;
        }
        return left;
    }

    /**
     * Determines the resulting {@link ExpressionType} of a multiplication operation
     * based on the operand types.
     *
     * <p>Multiplication is only valid when at least one operand is of type
     * {@link ExpressionType#SCALAR}, and neither operand is a non-numeric type
     * (BOOLEAN or COLOR).</p>
     *
     * <p>If the operands are incompatible, an error is recorded on the operation
     * and {@link ExpressionType#UNDEFINED} is returned.</p>
     *
     * @param op    the multiplication operation being checked
     * @param left  the type of the left operand
     * @param right the type of the right operand
     * @return the resulting {@link ExpressionType} favoring the non-scalar type, or {@link ExpressionType#UNDEFINED}
     * if the operands are not compatible
     */
    private ExpressionType resolveMultiplyOperation(Operation op, ExpressionType left, ExpressionType right) {
        boolean containsNoScalar = left != SCALAR && right != SCALAR;
        boolean containsNonNumeric = nonNumericTypes.contains(left) || nonNumericTypes.contains(right);

        if (containsNoScalar || containsNonNumeric) {
            op.setError("Can't Multiply " + left + " with " + right);
            return UNDEFINED;
        }

        return left == SCALAR ? right : left;
    }

    /**
     * Resolves the type of a variable reference by checking its presence in the current scope.
     * If the variable is not found, an error is recorded on the reference, and the type is set to {@link ExpressionType#UNDEFINED}.
     *
     * @param ref The {@link VariableReference} to be resolved, containing the name of the variable.
     * @return The {@link ExpressionType} of the variable if found in the current scope;
     * otherwise, {@link ExpressionType#UNDEFINED}.
     */
    private ExpressionType resolveVariableRef(VariableReference ref) {
        ExpressionType type = scopeManager.resolve(ref.name);
        if (type == null) {
            ref.setError("Unknown variable '" + ref.name + "'");
            return UNDEFINED;
        }
        return type;
    }

    /**
     * Checks whether a declaration targets a known property and assigns a value of an allowed type.
     *
     * <p>Records an error on {@code decl} when the property is unknown, the value is missing,
     * or the resolved expression type is not allowed for the property. If the expression resolves
     * to {@link ExpressionType#UNDEFINED}, no additional error is added.</p>
     *
     * @param decl the declaration to validate
     */
    private void handleDeclaration(Declaration decl) {
        String propertyName = decl.property.name;
        Set<ExpressionType> allowed = PropertyRules.allowedTypesFor(propertyName);

        if (allowed == null) {
            decl.setError("Unknown property '" + propertyName + "'");
            return;
        }

        if (decl.expression == null) {
            decl.setError("Property '" + propertyName + "' must have a value");
            return;
        }

        ExpressionType actualType = resolveExpressionType(decl.expression);
        if (actualType == UNDEFINED) {
            return;
        }

        if (!allowed.contains(actualType)) {
            decl.setError("Invalid value type '" + actualType + "' for property '" + propertyName + "'");
        }

    }

}