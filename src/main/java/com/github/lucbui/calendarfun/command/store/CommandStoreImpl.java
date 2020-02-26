package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.token.Tokenizer;
import com.github.lucbui.calendarfun.validation.CommandValidator;
import com.github.lucbui.calendarfun.validation.MessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class CommandStoreImpl implements CommandStore {

    private final Tokenizer tokenizer;
    private final MessageValidator messageValidator;
    private final CommandValidator commandValidator;
    private final Map<String, BotCommand> commandMap;

    @Autowired
    public CommandStoreImpl(Tokenizer tokenizer, MessageValidator messageValidator, CommandValidator commandValidator, CommandStoreMapFactory commandStoreMapFactory) {
        this.tokenizer = tokenizer;
        this.messageValidator = messageValidator;
        this.commandValidator = commandValidator;
        this.commandMap = commandStoreMapFactory.getMap();
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(messageValidator.validate(event)){
            return event.getMessage()
                    .getContent()
                    .filter(tokenizer::isValid)
                    .map(cmdMessage -> {
                        return commandMap.get(tokenizer.tokenize(cmdMessage).getCommand());
                    })
                    .map(cmd -> {
                        try {
                            if(commandValidator.validate(event, cmd)) {
                                return cmd.getBehavior().execute(event);
                            } else {
                                return Mono.<Void>empty();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            event.getMessage().getChannel()
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
