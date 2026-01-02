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

    private ScopeManager<ExpressionType> scopeManager;

    private final Map<String, Set<ExpressionType>> allowedTypes = Map.of(
            "width", Set.of(PERCENTAGE, PIXEL),
            "height", Set.of(PIXEL, PERCENTAGE),
            "color", Set.of(COLOR),
            "background-color", Set.of(COLOR)
    );

    public void check(AST ast) {
        // TODO: Make checker stateless; it's not because of this
        scopeManager = new ScopeManager<>();
        scopeManager.inNewScope(() -> checkBody(ast.root.getChildren()));
    }

    private void checkBody(List<ASTNode> body) {
        for (ASTNode child : body) {
            switch (child) {
                case VariableAssignment va -> handleVariableAssignment(va);
                case Declaration decl -> handleDeclaration(decl);
                case IfClause ifc -> handleIfClause(ifc);
                case StyleRule rule -> handleStyleRule(rule);
                default -> { // skip
                }
            }
        }
    }

    private void handleStyleRule(StyleRule rule) {
        scopeManager.inNewScope(() -> checkBody(rule.getChildren()));
    }

    private void handleIfClause(IfClause ifc) {
        checkIfCondition(ifc);

        scopeManager.inNewScope(() -> checkBody(ifc.body));

        if (ifc.elseClause != null) {
            scopeManager.inNewScope(() -> checkBody(ifc.elseClause.getChildren()));
        }
    }

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

    private void handleVariableAssignment(VariableAssignment varAss) {
        ExpressionType type = resolveExpressionType(varAss.expression);
        String varName = varAss.name.name;
        if (!scopeManager.declareIfAbsent(varName, type)) {
            varAss.setError("Variable '" + varName + "' redeclared in the same scope");
        }
    }

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

    private ExpressionType resolveAdditiveOperation(Operation op, ExpressionType left, ExpressionType right) {
        if (left == right
                && (left == PERCENTAGE || left == SCALAR || left == PIXEL)) {
            return left;
        }
        op.setError("Invalid operands for " + op.getNodeLabel() + ": " + left + " and " + right);
        return UNDEFINED;
    }

    private ExpressionType resolveMultiplyOperation(Operation op, ExpressionType left, ExpressionType right) {
        boolean hasNonMathType = left == BOOL || right == BOOL || left == COLOR || right == COLOR;
        boolean containsNoScalar = left != SCALAR && right != SCALAR;

        if (hasNonMathType || containsNoScalar) {
            op.setError("Can't Multiply " + left + " with " + right);
            return UNDEFINED;
        }

        return left == SCALAR ? right : left;
    }

    private ExpressionType resolveVariableRef(VariableReference ref) {
        ExpressionType type = scopeManager.resolve(ref.name);
        if (type == null) {
            ref.setError("Unknown variable '" + ref.name + "'");
            return UNDEFINED;
        }
        return type;
    }

    private void handleDeclaration(Declaration decl) {
        String propertyName = decl.property.name;
        Set<ExpressionType> allowed = allowedTypes.get(propertyName);

        if (allowed == null) {
            decl.setError("Unknown property '" + propertyName + "'");
            return;
        }

        if (decl.expression == null) {
            decl.setError("Property '" + propertyName + "' must have a value");
            return;
        }

        ExpressionType actualType = resolveExpressionType(decl.expression);
        if (actualType == UNDEFINED || allowed.contains(actualType)) {
            return;
        }

        decl.setError("Invalid value type '" + actualType + "' for property '" + propertyName + "'");
    }

}

