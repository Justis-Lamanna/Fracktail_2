package com.github.lucbui.bot.cmds;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.annotation.Permissions;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Commands
public class BotUseCommands {
    @Autowired
    private CommandList commandList;

    @Autowired
    @Qualifier("userPermissionValidator")
    private UserPermissionValidator userPermissionValidator;

    private Instant startTime;

    @PostConstruct
    private void postConstruct() {
        this.startTime = Instant.now();
    }

    @Command(help = "Get help for any command. Usage is !help [command name without exclamation point].")
    public Mono<Void> help(MessageCreateEvent evt, @Param(0) String cmd) {
        String cmdToSearch = (cmd == null) ? "help" : cmd;
        BotCommand command = commandList.getCommand(cmdToSearch);
        return Mono.justOrEmpty(command)
                .filterWhen(c -> userPermissionValidator.validate(evt, c))
                .map(Optional::of).defaultIfEmpty(Optional.empty())
                .flatMap(c -> DiscordUtils.respond(evt.getMessage(), c.map(BotCommand::getHelpText).orElse(cmdToSearch + " is not a valid command.")));
    }

    @Command(help = "Get a list of all usable commands.")
    public Mono<Void> commands(MessageCreateEvent evt) {
        return Flux.fromIterable(commandList.getAllCommands())
                .filterWhen(c -> userPermissionValidator.validate(evt, c))
                .flatMap(c -> Flux.just(c.getNames()))
                .sort()
                .map(c -> "!" + c)
                .collect(Collectors.joining(", "))
                .flatMap(text -> DiscordUtils.respond(evt.getMessage(), "Commands are: " + text + "."));
    }

    @Command(help = "Get the uptime of this bot.")
    public String uptime() {
        Duration uptime = Duration.between(startTime, Instant.now());
        return "Bot has been up for " + DurationFormatUtils.formatDurationWords(uptime.toMillis(), true, true);
    }

    @Command(help = "Turn off the bot.")
    @Permissions("admin")
    public Mono<Void> sleep(MessageCreateEvent evt) {
        return DiscordUtils.respond(evt.getMessage(), "Okay. Good night!")
                .then(evt.getClient().logout());
    }
}
