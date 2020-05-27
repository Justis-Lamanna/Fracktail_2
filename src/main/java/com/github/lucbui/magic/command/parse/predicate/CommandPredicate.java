package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;

public interface CommandPredicate {
    boolean canUseInContext(CommandUseContext ctx);
    boolean canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx);

    static CommandPredicate identity() {
        return new CommandPredicate() {
            @Override
            public boolean canUseInContext(CommandUseContext ctx) {
                return true;
            }

            @Override
            public boolean canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
                return true;
            }
        };
    }
}
