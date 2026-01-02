package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.scoping.ScopeManager;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import javax.naming.ldap.ExtendedRequest;
import java.util.*;

public class Checker {

    ScopeManager<ExpressionType> scopeManager;

    private final Map<String, Set<ExpressionType>> allowedTypes = Map.of(
            "width", Set.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL),
            "height", Set.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE),
            "color", Set.of(ExpressionType.COLOR),
            "background-color", Set.of(ExpressionType.COLOR)
    );

    private final Map<Class<?>, ExpressionType> typeMap = Map.of(
            PixelLiteral.class, ExpressionType.PIXEL,
            ColorLiteral.class, ExpressionType.COLOR,
            PercentageLiteral.class, ExpressionType.PERCENTAGE,
            ScalarLiteral.class, ExpressionType.SCALAR,
            BoolLiteral.class, ExpressionType.BOOL
    );

    public void check(AST ast) {
        if (ast == null || ast.root == null) {
            throw new IllegalArgumentException("AST or root cannot be null");
        }

        scopeManager = new ScopeManager<>();

        try (var _ = scopeManager.enter()) {
            checkBody(ast.root.getChildren());
        }
    }

    private void checkBody(List<ASTNode> body) {
        for (ASTNode child : body) {
            if (child instanceof VariableAssignment va) {
                handleVariableAssignment(va);
                continue;
            }

            if (child instanceof Declaration decl) {
                checkDeclaration(decl);
                continue;
            }

            if (child instanceof StyleRule rule) {
                try (var _ = scopeManager.enter()) {
                    checkBody(rule.getChildren());
                }
                continue;
            }

            if (child instanceof IfClause ifc) {
                checkIfCondition(ifc);

                try (var _ = scopeManager.enter()) {
                    checkBody(ifc.body);
                }

                if (ifc.elseClause != null) {
                    try (var _ = scopeManager.enter()) {
                        checkBody(ifc.elseClause.getChildren());
                    }
                }
            }

        }
    }

    private void checkIfCondition(IfClause ifc) {
        ExpressionType type = resolveExpressionType(ifc.conditionalExpression);
        if (type != ExpressionType.BOOL) {
            ifc.setError("If-condition must be a boolean, but got: " + type);
        }
    }

    private void handleVariableAssignment(VariableAssignment varAss) {
        ExpressionType type = resolveExpressionType(varAss.expression);
        String varName = varAss.name.name;
        if (scopeManager.existsInCurrentScope(varName)) {
            varAss.setError("Variable '" + varName + "' redeclared in the same scope");
        }
        scopeManager.declare(varName, type);
    }

    private ExpressionType resolveExpressionType(Expression expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression was null. AST invariant violated.");
        }

        return switch (expr) {
            case VariableReference ref -> resolveVariableRef(ref);
            case Operation op -> resolveOperationType(op);
            case Literal lit -> resolveLiteralType(lit);
            default -> {
                expr.setError("Unsupported expression: " + expr.getClass().getSimpleName());
                yield ExpressionType.UNDEFINED;
            }
        };
    }

    private ExpressionType resolveLiteralType(Literal lit) {
        ExpressionType t = typeMap.get(lit.getClass());
        if (t != null) return t;

        lit.setError("Unknown literal type: " + lit.getClass().getSimpleName());
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType resolveOperationType(Operation op) {
        var left = resolveExpressionType(op.lhs);
        var right = resolveExpressionType(op.rhs);

        if (left == ExpressionType.UNDEFINED || right == ExpressionType.UNDEFINED) {
            return ExpressionType.UNDEFINED;
        }

        if (op instanceof AddOperation || op instanceof SubtractOperation) {
            if (left == right && (left == ExpressionType.PERCENTAGE || left == ExpressionType.SCALAR || left == ExpressionType.PIXEL)) {
                return left;
            }
            op.setError("Can't Subtract OR Add " + left.name() + " from " + right.name());
            return ExpressionType.UNDEFINED;
        }

        if (op instanceof MultiplyOperation) {
            if ((left == ExpressionType.SCALAR || right == ExpressionType.SCALAR)
                    && (left != ExpressionType.COLOR && right != ExpressionType.COLOR)
            && (left != ExpressionType.BOOL && right != ExpressionType.BOOL)) {
                return (left == ExpressionType.SCALAR) ? right : left;
            }
            op.setError("Can't Multiply " + left.name() + " with " + right.name());
            return ExpressionType.UNDEFINED;
        }

        op.setError("Unknown operation: " + op.getClass().getSimpleName());
        return ExpressionType.UNDEFINED;

    }

    private ExpressionType resolveVariableRef(VariableReference ref) {
        ExpressionType type = scopeManager.resolve(ref.name);
        if (type == null) {
            ref.setError("Unknown variable '" + ref.name + "'");
            return ExpressionType.UNDEFINED;
        }
        return type;
    }

    private void checkDeclaration(Declaration decl) {
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
        if (actualType == ExpressionType.UNDEFINED || allowed.contains(actualType)) {
            return;
        }

        decl.setError("Invalid value type '" + actualType + "' for property '" + propertyName + "'");
    }

}
