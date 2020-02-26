package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.token.Tokenizer;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class CommandStoreImpl implements CommandStore {

    private final Tokenizer tokenizer;
    private final Map<String, BotCommand> commandMap;

    @Autowired
    public CommandStoreImpl(Tokenizer tokenizer, CommandStoreMapFactory commandStoreMapFactory) {
        this.tokenizer = tokenizer;
        this.commandMap = commandStoreMapFactory.getMap();
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(event.getMessage().getAuthor().map(User::isBot).orElse(false)){
            return Mono.empty();
        }
        return event.getMessage()
                .getContent()
                .filter(tokenizer::isValid)
                .map(cmdMessage -> {
                    return commandMap.get(tokenizer.tokenize(cmdMessage).getCommand());
                })
                .map(cmd -> {
                    try {
                        return cmd.getBehavior().execute(event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        event.getMessage().getChannel()
                                .flatMap(channel -> channel.createMessage("I'm sorry, I encountered an exception. Please check the logs."))
                                .then();
                        return Mono.<Void>empty();
                    }
                }).orElse(Mono.empty());
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
