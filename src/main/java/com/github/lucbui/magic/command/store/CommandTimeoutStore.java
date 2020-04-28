package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;

import java.time.Duration;

public interface CommandTimeoutStore {
    Duration getTimeoutForCommand(BotCommand command);
    void setTimeoutForCommand(BotCommand command, Duration duration);
}
