package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandStore;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Commands
public class BotUseCommands {
    @Autowired
    private CommandStore commandStore;

    @Autowired
    private TranslateService translateService;

    private Instant startTime;

    @PostConstruct
    private void postConstruct() {
        this.startTime = Instant.now();
    }

    @Command
    @CommandParams(value = 1, comparison = ParamsComparison.OR_LESS)
    public Mono<String> help(MessageCreateEvent evt, @Param(0) @Default("help") String cmd) {
        return commandStore.getAllCommands(CommandUseContext.from(evt))
                .filter(bc -> StringUtils.equalsIgnoreCase(cmd, bc.getName()) || StringUtils.equalsAnyIgnoreCase(cmd, bc.getAliases()))
                .next()
                .flatMap(bc -> translateService.getStringMono(bc.getName() + ".help"))
                .switchIfEmpty( translateService.getFormattedStringMono("help.validation.unknownCommand", cmd));
    }


    @Command
    @CommandParams(0)
    public Mono<String> commands(MessageCreateEvent evt) {
        return commandStore.getAllCommands(CommandUseContext.from(evt))
                .map(BotCommand::getName)
                .distinct()
                .sort()
                .map(cmd -> "!" + cmd)
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

    @Command
    @CommandParams(0)
    @Permissions("owner")
    public Mono<Void> logs(MessageCreateEvent evt) {
        File file = new File("/home/pi/fracktail/logs/app.log");
        if(file.exists()) {
            return evt.getMessage().getChannel()
                    .flatMap(mc -> mc.createMessage(messageCreateSpec -> {
                        try {
                            messageCreateSpec.setContent("Here are the files!");
                            messageCreateSpec.addFile("app.log", new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            messageCreateSpec.setContent("Error attaching logs: " + e.getMessage());
                        }
                    }))
                    .then();
        }
        return DiscordUtils.respond(evt.getMessage(), "I can't get the logs on this environment.");
    }

    @Command
    @CommandParams(0)
    public Mono<Void> butt(MessageCreateEvent evt) {
        Snowflake origMessageUserId = evt.getMessage().getAuthor().map(User::getId).orElse(null);
        if(origMessageUserId == null){
            return Mono.empty();
        }
        return evt.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage(DiscordUtils.getMentionFromId(origMessageUserId) + ", react to this message with \uD83C\uDF51 to proceed."))
                .cache()
                .repeat()
                .zipWith(evt.getClient().getEventDispatcher().on(ReactionAddEvent.class))
                .filter(tuple -> {
                    Snowflake origChannel = tuple.getT1().getChannelId();
                    Snowflake origMessage = tuple.getT1().getId();
                    ReactionAddEvent reactionAddEvent = tuple.getT2();
                    String rawUnicode = reactionAddEvent.getEmoji().asUnicodeEmoji().map(ReactionEmoji.Unicode::getRaw).orElse(null);
                    return reactionAddEvent.getChannelId().equals(origChannel) &&
                            reactionAddEvent.getMessageId().equals(origMessage) &&
                            reactionAddEvent.getUserId().equals(origMessageUserId) &&
                            "\uD83C\uDF51".equals(rawUnicode);
                })
                .next()
                .flatMap(tuple -> tuple.getT2().getChannel()
                        .flatMap(channel ->
                            channel.createMessage("Nice butt, " + DiscordUtils.getMentionFromId(tuple.getT2().getUserId()) + "!")))
                .then();
    }
}

