package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class CommandList {
    private final Map<String, BotCommand> commandMap;

    @Autowired
    public CommandList(CommandStoreMapFactory commandStoreMapFactory) {
        this.commandMap = commandStoreMapFactory.getMap();
    }

    public void addCommand(BotCommand command) {
        Arrays.stream(command.getNames())
                .forEach(name -> commandMap.put(name, command));
    }

    public void removeCommand(String... names) {
        Arrays.stream(names)
                .forEach(commandMap::remove);
    }

    public BotCommand getCommand(String name) {
        return commandMap.get(name);
    }

    public List<BotCommand> getAllCommands() {
        return new ArrayList<>(commandMap.values());
    }
}
