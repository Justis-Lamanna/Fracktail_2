package com.github.lucbui.magic.command.func;

import java.lang.reflect.Method;

public interface BotCommandPostProcessor {
    void process(Method method, BotCommand botCommand);
}
