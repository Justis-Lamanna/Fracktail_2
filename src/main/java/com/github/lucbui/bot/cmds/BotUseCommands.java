package com.github.lucbui.bot.cmds;

import com.github.lucbui.magic.annotation.BasicSender;
import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.validation.PermissionsService;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Commands
public class BotUseCommands {
    @Autowired
    private CommandList commandList;

    @Autowired
    @Qualifier("userPermissionValidator")
    private UserPermissionValidator userPermissionValidator;

    @Command(help = "Get help for any command. Usage is !help [command name without exclamation point].")
    public String help(MessageCreateEvent evt, @Param(0) String cmd) {
        if(cmd == null) {
            cmd = "help";
        }
        BotCommand command = commandList.getCommand(cmd);
        if(command == null || !userPermissionValidator.validate(evt, command)) {
            return cmd + " is not a valid command.";
        } else {
            return command.getHelpText();
        }
    }

    @Command(help = "Get a list of all usable commands.")
    public String commands(MessageCreateEvent evt) {
        return "Commands are: " + commandList.getAllCommands()
                .stream()
                .filter(cmd -> userPermissionValidator.validate(evt, cmd))
                .flatMap(cmd -> Arrays.stream(cmd.getNames()))
                .sorted()
                .map(cmd -> "!" + cmd)
                .collect(Collectors.joining(", ")) + ".";
    }
}
