package nl.han.ica.icss.ast;

import nl.han.ica.icss.checker.SemanticError;

import java.util.ArrayList;
import java.util.Objects;

public class AST {
	//The root of the tree
	public StyleSheet root;

	public AST() {
		root = new StyleSheet();
	}
	public AST(StyleSheet stylesheet) {
		root = stylesheet;
	}
	public void setRoot(StyleSheet stylesheet) {
		root = stylesheet;
	}

	public ASTNode getRoot() {
		if(root == null) throw new IllegalStateException("AST root is not set");
		return root;
	}

    public ArrayList<SemanticError> getErrors() {
	    ArrayList<SemanticError> errors = new ArrayList<>();
        collectErrors(errors,root);
        return errors;
    }
    private void collectErrors(ArrayList<SemanticError> errors, ASTNode node) {
	    if(node.hasError()) {
	        errors.add(node.getError());
        }
        for(ASTNode child: node.getChildren()) {
	        collectErrors(errors,child);
        }
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AST ast = (AST) o;
		return Objects.equals(root, ast.root);
	}

	@Override
	public int hashCode() {
		return Objects.hash(root);
	}


	@Override
	public String toString() {
		if (root == null) return "AST is empty";
		return prettyPrintNode(root, 0);
	}

	private String prettyPrintNode(ASTNode node, int indentLevel) {
		StringBuilder sb = new StringBuilder();
		String indent = "  ".repeat(indentLevel);

		// Print current node
		sb.append(indent)
				.append(node.getClass().getSimpleName());

		if (node.getNodeLabel() != null) {
			sb.append(" (").append(node.getNodeLabel()).append(")");
		}

		sb.append("\n");

		// Recursively print children
		for (ASTNode child : node.getChildren()) {
			sb.append(prettyPrintNode(child, indentLevel + 1));
		}

		return sb.toString();
	}
}
