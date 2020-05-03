package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.annotation.Permissions;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
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

@Commands
public class BotUseCommands {
    @Autowired
    private CommandList commandList;

    @Autowired
    private UserPermissionValidator userPermissionValidator;

    @Autowired
    private TranslateService translateService;

    private Instant startTime;

    @PostConstruct
    private void postConstruct() {
        this.startTime = Instant.now();
    }

    @Command
    public Mono<String> help(MessageCreateEvent evt, @Param(0) String cmd) {
        String cmdToSearch = (cmd == null) ? "help" : cmd;
        BotCommand command = commandList.getCommand(cmdToSearch);
        return Mono.justOrEmpty(command)
                .filterWhen(c -> userPermissionValidator.validate(evt, c))
                .map(Optional::of).defaultIfEmpty(Optional.empty())
                .map(c -> c.map(BotCommand::getHelpText).map(translateService::getString)
                            .orElse(translateService.getFormattedString("help.validation.unknownCommand", cmd)));
    }

    @Command
    public Mono<String> commands(MessageCreateEvent evt) {
        return Flux.fromIterable(commandList.getAllCommands())
                .filterWhen(c -> userPermissionValidator.validate(evt, c))
                .flatMap(c -> Mono.just(c.getName()))
                .distinct()
                .sort()
                .map(c -> "!" + c)
                .collect(Collectors.joining(", "))
                .map(text -> translateService.getFormattedString("commands.text", text));
    }

    @Command
    public String uptime() {
        Duration uptime = Duration.between(startTime, Instant.now());
        return translateService.getFormattedString("uptime.text", uptime.getSeconds());
    }

    @Command
    @Permissions("owner")
    public Mono<Void> sleep(MessageCreateEvent evt) {
        return DiscordUtils.respond(evt.getMessage(), translateService.getString("sleep.text"))
                .then(evt.getClient().logout());
    }
}
