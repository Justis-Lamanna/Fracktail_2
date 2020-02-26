package com.github.lucbui.calendarfun.command;

import com.github.lucbui.calendarfun.annotation.Command;
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
    @Autowired
    private Tokenizer tokenizer;

    private Map<String, BotCommand> commandMap = new HashMap<>();

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(event.getMessage().getAuthor().map(User::isBot).orElse(false)){
            return Mono.empty();
        }
        return event.getMessage()
                .getContent()
                .filter(message -> tokenizer.isValid(message))
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
                .forEach(name -> commandMap.remove(name));
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
