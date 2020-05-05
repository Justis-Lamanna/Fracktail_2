package com.github.lucbui.magic.command.func.postprocessor;

import com.github.lucbui.magic.annotation.Timeout;
import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.command.store.CommandTimeoutStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.Duration;

public class TimeoutPostProcessor implements BotCommandPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutPostProcessor.class);

    private CommandTimeoutStore commandTimeoutStore;

    public TimeoutPostProcessor(CommandTimeoutStore commandTimeoutStore) {
        this.commandTimeoutStore = commandTimeoutStore;
    }

    @Override
    public void process(Method method, BotCommand botCommand, CommandCreateContext ctx) {
        if(method.isAnnotationPresent(Timeout.class)) {
            Timeout timeout = method.getAnnotation(Timeout.class);
            Duration timeoutDuration = Duration.of(timeout.value(), timeout.unit());
            if(!timeoutDuration.isZero()) {
                commandTimeoutStore.setTimeoutForCommand(botCommand, timeoutDuration);
                LOGGER.debug("Set timeout for {} to {}", botCommand.getName(), timeoutDuration);
            }
        }
    }
}