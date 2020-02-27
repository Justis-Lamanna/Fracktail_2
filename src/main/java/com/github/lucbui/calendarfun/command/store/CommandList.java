package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandList {
    private final List<BotCommand> commands;

    public CommandList() {
        this.commands = new ArrayList<>();
    }

    public void addCommand(BotCommand command) {
        this.commands.add(command);
    }

    public List<BotCommand> getCommands() {
        return commands;
    }
}
