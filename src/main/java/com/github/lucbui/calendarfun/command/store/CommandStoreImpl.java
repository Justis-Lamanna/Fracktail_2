package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.token.Tokenizer;
import com.github.lucbui.calendarfun.validation.CommandValidator;
import com.github.lucbui.calendarfun.validation.MessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.*;

public class CommandStoreImpl implements CommandStore {

    private final Tokenizer tokenizer;
    private final List<MessageValidator> messageValidators;
    private final List<CommandValidator> commandValidators;
    private final Map<String, BotCommand> commandMap;

    public CommandStoreImpl(Tokenizer tokenizer, List<MessageValidator> messageValidators, List<CommandValidator> commandValidators, Map<String, BotCommand> commandMap) {
        this.tokenizer = tokenizer;
        this.messageValidators = messageValidators;
        this.commandValidators = commandValidators;
        this.commandMap = commandMap;
    }

    private boolean validateMessage(MessageCreateEvent event) {
        return messageValidators.stream()
                .allMatch(validator -> validator.validate(event));
    }

    private boolean validateCommand(MessageCreateEvent event, BotCommand command) {
        return commandValidators.stream()
                .allMatch(validator -> validator.validate(event, command));
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(validateMessage(event)){
            return event.getMessage()
                    .getContent()
                    .filter(tokenizer::isValid)
                    .map(cmdMessage -> {
                        return commandMap.get(tokenizer.tokenize(cmdMessage).getCommand());
                    })
                    .map(cmd -> {
                        try {
                            if(validateCommand(event, cmd)) {
                                return cmd.getBehavior().execute(event);
                            } else {
                                return Mono.<Void>empty();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            event.getMessage()
                                    .getChannel()
                                    .flatMap(channel -> channel.createMessage("I'm sorry, I encountered an exception. Please check the logs."))
                                    .then();
                            return Mono.<Void>empty();
                        }
                    }).orElse(Mono.empty());
        }
        return Mono.empty();
    }

    @Override
    public void addCommand(BotCommand command) {
        Arrays.stream(command.getNames())
                .forEach(name -> commandMap.put(name, command));
    }

    @Override
    public void removeCommand(String... names) {
        Arrays.stream(names)
                .forEach(commandMap::remove);
    }

    @Override
    public BotCommand getCommand(String name) {
        return commandMap.get(name);
    }

    @Override
    public List<BotCommand> getAllCommands() {
        return new ArrayList<>(commandMap.values());
    }
}
