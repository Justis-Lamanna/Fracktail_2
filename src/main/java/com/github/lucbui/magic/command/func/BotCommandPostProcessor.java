package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.command.context.CommandCreateContext;

import java.lang.reflect.Method;

public interface BotCommandPostProcessor {
    void process(Method method, BotCommand botCommand, CommandCreateContext ctx);
}
