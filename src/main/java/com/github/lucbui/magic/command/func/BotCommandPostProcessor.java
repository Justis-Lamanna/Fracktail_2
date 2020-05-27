package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.execution.BCommand;

import java.lang.reflect.Method;

public interface BotCommandPostProcessor {
    void process(Method method, BCommand botCommand, CommandCreateContext ctx);
}
