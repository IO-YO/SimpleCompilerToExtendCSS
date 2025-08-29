package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import org.jetbrains.annotations.NotNull;

import static nl.han.ica.icss.ASTBuilder.ASTBuilder.*;

public class EvaluatorTestBuilder {

    public record ASTPair(AST input, AST expected) {}

    public static Builder build() {
        return new Builder();
    }

    public static class Builder {
        private AST input;
        private AST expected;

        public Builder input(@NotNull ASTNode... nodes) {
            this.input = stylesheet(nodes);
            return this;
        }

        public Builder input(AST ast) {
            this.input = ast;
            return this;
        }

        public Builder expected(@NotNull ASTNode... nodes) {
            this.expected = stylesheet(nodes);
            return this;
        }

        public Builder expected(AST ast) {
            this.expected = ast;
            return this;
        }

        public ASTPair toPair() {
            return new ASTPair(input, expected);
        }
    }
}
