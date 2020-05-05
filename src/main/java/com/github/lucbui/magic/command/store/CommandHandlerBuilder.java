package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.validators.ChainCreateMessageValidator;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class CommandHandlerBuilder {
    private Tokenizer tokenizer;
    private CommandStore commandList;
    private List<CreateMessageValidator> validators;
    private NoCommandFoundHandler noCommandFoundHandler;

    public CommandHandlerBuilder(Tokenizer tokenizer, CommandStore commandList) {
        this.tokenizer = tokenizer;
        this.commandList = commandList;
        this.validators = new ArrayList<>();
        this.noCommandFoundHandler = NoCommandFoundHandler.doNothing();
    }

    public CommandHandlerBuilder withTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public CommandHandlerBuilder withCommandList(CommandStore commandList) {
        this.commandList = commandList;
        return this;
    }

    public CommandHandlerBuilder withValidator(CreateMessageValidator validator) {
        this.validators.add(validator);
        return this;
    }

    public CommandHandlerBuilder withValidators(List<CreateMessageValidator> validators) {
        this.validators.addAll(validators);
        return this;
    }

    public CommandHandlerBuilder withNoCommandFoundHandler(NoCommandFoundHandler noCommandFoundHandler) {
        this.noCommandFoundHandler = noCommandFoundHandler;
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
        return new DefaultCommandHandler(tokenizer, validator, commandList, noCommandFoundHandler);
    }
}
