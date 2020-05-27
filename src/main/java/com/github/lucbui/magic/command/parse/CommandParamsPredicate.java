package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.annotation.ParamsComparison;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;

import java.util.function.BiPredicate;

public class CommandParamsPredicate implements BiPredicate<Tokens, CommandUseContext> {
    private int value;
    private ParamsComparison comparison;

    public CommandParamsPredicate(int value, ParamsComparison comparison) {
        this.value = value;
        this.comparison = comparison;
    }

    @Override
    public boolean test(Tokens tokens, CommandUseContext commandUseContext) {
        switch (comparison) {
            case OR_MORE: return tokens.getParams().length >= value;
            case OR_LESS: return tokens.getParams().length <= value;
            default: return tokens.getParams().length == value;
        }
    }
}
