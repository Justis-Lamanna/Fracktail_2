package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
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
    @CommandParams(value = 1, comparison = ParamsComparison.OR_LESS)
    public Mono<String> help(MessageCreateEvent evt, @Param(0) String cmd) {
        String cmdToSearch = (cmd == null) ? "help" : cmd;
        return Flux.fromIterable(commandList.getCommandsForName(cmdToSearch))
                .filterWhen(bc -> userPermissionValidator.validate(evt, bc))
                .collectList()
                .flatMap(commands -> commands.isEmpty() ?
                        translateService.getFormattedStringMono("help.validation.unknownCommand", cmd) :
                        translateService.getStringMono(commands.get(0).getName() + ".help"));
    }

    @Command
    @CommandParams(0)
    public Mono<String> commands(MessageCreateEvent evt) {
        return Flux.fromIterable(commandList.getAllCommands())
                .filterWhen(c -> userPermissionValidator.validate(evt, c))
                .map(BotCommand::getName)
                .distinct()
                .sort()
                .map(c -> "!" + c)
                .collect(Collectors.joining(", "))
                .map(text -> translateService.getFormattedString("commands.text", text));
    }

    @Command
    @CommandParams(0)
    public String uptime() {
        Duration uptime = Duration.between(startTime, Instant.now());
        return translateService.getFormattedString("uptime.text", uptime.getSeconds());
    }

    @Command
    @CommandParams(0)
    @Permissions("owner")
    public Mono<Void> sleep(MessageCreateEvent evt) {
        return DiscordUtils.respond(evt.getMessage(), translateService.getString("sleep.text"))
                .then(evt.getClient().logout());
    }
}
