package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.annotation.ParamsComparison;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;

public class ParameterCountCommandPredicate implements CommandPredicate {
    private final int value;
    private final ParamsComparison comparison;

    public ParameterCountCommandPredicate(int value, ParamsComparison comparison) {
        this.value = value;
        this.comparison = comparison;
    }

    @Override
    public boolean canUseInContext(CommandUseContext ctx) {
        return true;
    }

    @Override
    public boolean canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        int l = tokens.getParams() == null ? 0 : tokens.getParams().length;
        switch (comparison) {
            case OR_MORE: return l >= value;
            case OR_LESS: return l <= value;
            default: return l == value;
        }
    }
}
