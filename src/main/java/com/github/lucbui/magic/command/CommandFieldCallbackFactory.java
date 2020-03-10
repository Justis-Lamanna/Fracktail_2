package com.github.lucbui.magic.command;

import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.token.Tokenizer;

/**
 * A factory which creates a CommandFieldCallback for each processed bean
 */
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
