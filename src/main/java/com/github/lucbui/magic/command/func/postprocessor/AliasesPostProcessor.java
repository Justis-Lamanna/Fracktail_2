package com.github.lucbui.magic.command.func.postprocessor;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;

import java.lang.reflect.Method;

public class AliasesPostProcessor implements BotCommandPostProcessor {
    @Override
    public void process(Method method, BotCommand botCommand) {
        botCommand.setAliases(method.getAnnotation(Command.class).aliases());
    }
}
