package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.execution.BCommand;

import java.lang.reflect.Method;

public interface BotCommandProcessor {
    void beforeCreate(Method method, CommandCreateContext ctx);
    void beforeUpdate(Method method, CommandCreateContext ctx);
    void afterCreate(Method method, BCommand newCommand, CommandCreateContext ctx);
    void afterUpdate(Method method, BCommand updatedCommand, CommandCreateContext ctx);
}
