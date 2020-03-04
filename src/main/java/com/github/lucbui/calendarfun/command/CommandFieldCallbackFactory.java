package com.github.lucbui.calendarfun.command;

import com.github.lucbui.calendarfun.command.store.CommandList;
import com.github.lucbui.calendarfun.token.Tokenizer;

public class CommandFieldCallbackFactory {
    private final CommandList commands;
    private final Tokenizer tokenizer;

    public CommandFieldCallbackFactory(CommandList commands, Tokenizer tokenizer) {
        this.commands = commands;
        this.tokenizer = tokenizer;
    }

    public CommandFieldCallback getCommandFieldCallback(Object bean) {
        return new CommandFieldCallback(commands, tokenizer, bean);
    }
}
