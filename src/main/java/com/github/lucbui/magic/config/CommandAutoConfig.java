package com.github.lucbui.magic.config;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.annotation.Sender;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.validation.user.UserValidator;
import discord4j.core.object.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnBean({CommandList.class, UserValidator.class})
@Commands
public class CommandAutoConfig {
    @Autowired
    private CommandList commandList;

    @Autowired
    private UserValidator userValidator;

    @Command(help = "Get help for any command. Usage is !help [command name without exclamation point].")
    public String help(@Param(0) String cmd, @Sender Member user) {
        if(cmd == null) {
            cmd = "help";
        }
        BotCommand command = commandList.getCommand(cmd);
        if(command == null || !userValidator.validate(user, command)) {
            return cmd + " is not a valid command.";
        } else {
            return command.getHelpText();
        }
    }

    @Command(help = "Get a list of all usable commands.")
    public String commands(@Sender Member user) {
        return "Commands are: " + commandList.getAllCommands()
                .stream()
                .filter(cmd -> userValidator.validate(user, cmd))
                .flatMap(cmd -> Arrays.stream(cmd.getNames()))
                .sorted()
                .map(cmd -> "!" + cmd)
                .collect(Collectors.joining(", ")) + ".";
    }
}
