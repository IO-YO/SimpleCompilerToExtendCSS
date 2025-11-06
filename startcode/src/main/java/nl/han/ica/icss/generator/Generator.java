package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.List;
import java.util.Objects;

public class Generator {

	public String generate(AST ast) {
		String css = "";
		css = generateStyleSheet(ast.root.body);
        return css;
	}

    private String generateStyleSheet(List<ASTNode> body) {
		StringBuilder sheet = new StringBuilder();
		for(ASTNode child : body) {
            if (child instanceof StyleRule) {
                sheet.append(generateStyleRule(child));
            }
		}

		return sheet.toString();

    }

    private String generateStyleRule(ASTNode rule) {
		StringBuilder cssRule = new StringBuilder();
		for(ASTNode child : rule.getChildren()) {
			if(child instanceof Selector s) {
				switch(s) {
					case TagSelector tag -> cssRule.append(tag.tag);
					case IdSelector id -> cssRule.append("#").append(id.id);
					case ClassSelector cls -> cssRule.append(".").append(cls.cls);
					default -> {}
				}
			}
		}


		cssRule.append(" {").append('\n');
		for(ASTNode child : rule.getChildren()) {
			if(child instanceof Declaration) {

			}
		}

		cssRule.append("}");

        return cssRule.toString();
    }
}
