package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.token.Tokenizer;
import com.github.lucbui.calendarfun.validation.CommandValidator;
import com.github.lucbui.calendarfun.validation.MessageValidator;

import java.util.*;

public class CommandStoreBuilder {
    private Tokenizer tokenizer;
    private List<MessageValidator> messageValidators;
    private List<CommandValidator> commandValidators;
    private CommandStoreMapFactory commandStoreMapFactory;
    private Map<String, BotCommand> commandMap;

    public CommandStoreBuilder(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.messageValidators = new ArrayList<>();
        this.commandValidators = new ArrayList<>();
        this.commandStoreMapFactory = HashMap::new;
        this.commandMap = new HashMap<>();
    }

    public CommandStoreBuilder setMessageValidators(MessageValidator... validators) {
        this.messageValidators = new ArrayList<>(Arrays.asList(validators));
        return this;
    }

    public CommandStoreBuilder setCommandValidators(CommandValidator... validators) {
        this.commandValidators = new ArrayList<>(Arrays.asList(validators));
        return this;
    }

    public CommandStoreBuilder setCommandStoreMapFactory(CommandStoreMapFactory commandStoreMapFactory) {
        this.commandStoreMapFactory = commandStoreMapFactory;
        return this;
    }

    public CommandStoreBuilder addCommand(BotCommand command, String... names) {
        Arrays.stream(names)
                .forEach(name -> this.commandMap.put(name, command));
        return this;
    }

    public CommandStore build() {
        Map<String, BotCommand> commands = this.commandStoreMapFactory.getMap();
        commands.putAll(this.commandMap);
        return new MapCommandStore(tokenizer, messageValidators, commandValidators, commandMap);
    }
}
