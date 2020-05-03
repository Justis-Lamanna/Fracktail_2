package com.github.lucbui.magic.command.func.postprocessor;

import com.github.lucbui.magic.annotation.CommandParams;
import com.github.lucbui.magic.annotation.ParamsComparison;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokens;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class ParametersPostProcessor implements BotCommandPostProcessor {
    @Override
    public void process(Method method, BotCommand botCommand) {
        if(method.isAnnotationPresent(CommandParams.class)) {
            CommandParams paramDefinition = method.getAnnotation(CommandParams.class);
            int numberOfParams = paramDefinition.value();
            ParamsComparison comparison = paramDefinition.comparison();
            botCommand.setTokensPredicate(getTokensPredicate(numberOfParams, comparison));
        }
    }

    private Predicate<Tokens> getTokensPredicate(int numberOfParams, ParamsComparison comparison) {
        switch(comparison) {
            case EXACTLY: return tokens -> tokens.getParams().length == numberOfParams;
            case OR_LESS: return tokens -> tokens.getParams().length <= numberOfParams;
            case OR_MORE: return tokens -> tokens.getParams().length >= numberOfParams;
            default: throw new BotException("Unable to get tokensPredicate from: " + numberOfParams + " " + comparison);
        }
    }
}
