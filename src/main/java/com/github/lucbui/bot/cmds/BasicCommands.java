package com.github.lucbui.bot.cmds;

import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.bot.calendar.CalendarService;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.validation.user.UserValidator;
import discord4j.core.object.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Commands
public class BasicCommands {
    @Autowired
    private CommandList commandList;

    @Autowired
    private UserValidator userValidator;

    @Command(help = "Perform arithmetic. Usage is !math [expression]")
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    public String math() {
        return "The answer is 3";
    }

    @Command(help = "Taunt the others in your server with a command they can't use")
    @Permissions("admin")
    public String admin() {
        return "This is a cool command that only admins can use!";
    }

    @Command(help = "RAFO!")
    @Timeout(value = 5, unit = ChronoUnit.MINUTES)
    public String rafo() {
        return "<:rafo1:596138147285434415><:rafo2:596138147797270538><:rafo3:596138379603869697><:rafo4:596138380132089879>\n" +
                "<:rafo5:596138380211781641><:rafo6:596138491469889536><:rafo7:596138588584804373><:rafo8:596138610193858581>\n" +
                "<:rafo9:596138646130917376><:rafo10:596138678108291082><:rafo11:596138697607348257><:rafo12:596138718817943552>\n" +
                "<:rafo13:596138741052211210><:rafo14:596138758160515073><:rafo15:596138771779682315><:rafo16:596138788984586268>";
    }

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
