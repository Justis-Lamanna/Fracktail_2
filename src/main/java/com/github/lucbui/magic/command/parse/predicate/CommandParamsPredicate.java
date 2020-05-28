package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.annotation.ParamsComparison;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;

import java.util.function.BiPredicate;

public class CommandParamsPredicate implements BiPredicate<Tokens, CommandUseContext> {
    private final BiPredicate<Tokens, CommandUseContext> predicate;

    public CommandParamsPredicate(int value, ParamsComparison comparison) {
        this.predicate = getPredicate(value, comparison);
    }

    private BiPredicate<Tokens, CommandUseContext> getPredicate(int value, ParamsComparison comparison) {
        switch (comparison) {
            case OR_MORE: return (t, c) -> t.getParams().length >= value;
            case OR_LESS: return (t, c) -> t.getParams().length <= value;
            default:      return (t, c) -> t.getParams().length == value;
        }
    }

    @Override
    public boolean test(Tokens tokens, CommandUseContext commandUseContext) {
        return predicate.test(tokens, commandUseContext);
    }
}
