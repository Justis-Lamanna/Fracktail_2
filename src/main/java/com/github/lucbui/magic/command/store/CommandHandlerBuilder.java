package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.validators.ChainCreateMessageValidator;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class CommandHandlerBuilder {
    private Tokenizer tokenizer;
    private CommandList commandList;
    private List<CreateMessageValidator> validators;

    public CommandHandlerBuilder(Tokenizer tokenizer, CommandList commandList) {
        this.tokenizer = tokenizer;
        this.commandList = commandList;
        this.validators = new ArrayList<>();
    }

    public CommandHandlerBuilder withTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public CommandHandlerBuilder withCommandList(CommandList commandList) {
        this.commandList = commandList;
        return this;
    }

    public CommandHandlerBuilder withValidator(CreateMessageValidator validator) {
        this.validators.add(validator);
        return this;
    }

    public CommandHandler build() {
        CreateMessageValidator validator;
        if(validators.isEmpty()) {
            validator = (event, command) -> Mono.just(true);
        } else if(validators.size() == 1) {
            validator = validators.get(0);
        } else {
            validator = new ChainCreateMessageValidator(validators);
        }
        return new DefaultCommandHandler(tokenizer, validator, commandList);
    }
}
