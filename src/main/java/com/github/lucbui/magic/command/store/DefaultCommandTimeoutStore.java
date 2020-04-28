package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultCommandTimeoutStore implements CommandTimeoutStore {
    private Map<BotCommand, Duration> mapping = Collections.synchronizedMap(new HashMap<>());
    private Duration defaultDuration;

    public DefaultCommandTimeoutStore(Duration defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    @Override
    public Duration getTimeoutForCommand(BotCommand command) {
        return mapping.computeIfAbsent(command, cmd -> defaultDuration);
    }

    @Override
    public void setTimeoutForCommand(BotCommand command, Duration duration) {
        mapping.put(command, duration);
    }
}
