package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandStore;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.util.DiscordUtils;
import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Component;
import com.profesorfalken.jsensors.model.components.Components;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.http.client.ClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Commands
public class BotUseCommands {
    private static final Pattern AT_SIGN = Pattern.compile("@");
    public static final float BYTES_IN_A_MB = 1048576f;
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
                .flatMap(bc -> {
                    String help = translateService.getString(TranslateHelper.helpKey(bc.getName()));
                    String usage = translateService.getString(TranslateHelper.usageKey(bc.getName()));
                    return Mono.just(help + "\n" + usage);
                })
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
    @CommandParams(1)
    public Mono<String> msg(MessageCreateEvent evt, @Param(0) String snowflake) {
        String sender = evt.getMessage().getAuthor().map(User::getUsername)
                .orElseThrow(() -> new BotException("Sender somehow had no author"));

        return Mono.justOrEmpty(DiscordUtils.toSnowflakeFromMentionOrLiteral(snowflake))
                .flatMap(s -> evt.getClient().getUserById(s))
                .zipWhen(User::getPrivateChannel)
                .flatMap(tuple -> tuple.getT2().createMessage(translateService.getFormattedString("msg.toUser", sender)).thenReturn(tuple.getT1()))
                .map(usr -> translateService.getFormattedString("msg.toSender", usr.getUsername()))
                .defaultIfEmpty(translateService.getString("validation.unknownUser"))
                .onErrorResume(DiscordUtils.ON_FORBIDDEN, e -> translateService.getStringMono("msg.validation.cantDm"));
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
    @Permissions("owner")
    public Mono<Void> stats(MessageCreateEvent evt) {
        return evt.getMessage().getChannel()
                .flatMapMany(c -> c.createMessage(spec -> {
                    spec.setEmbed(embed -> {
                        embed.setTitle(translateService.getString("stats.title"));
                        embed.setColor(new Color(183, 20, 65));
                        embed.setUrl("https://github.com/Justis-Lamanna/Fracktail_2");

                        embed.addField(
                                translateService.getString("stats.label.pid"),
                                AT_SIGN.split(ManagementFactory.getRuntimeMXBean().getName())[0], true);
                        embed.addField(
                                translateService.getString("stats.label.arch"),
                                Objects.toString(ManagementFactory.getOperatingSystemMXBean().getArch(), "?"), true);
                        embed.addField(
                                translateService.getString("stats.label.osName"),
                                Objects.toString(ManagementFactory.getOperatingSystemMXBean().getName(), "?"), true);
                        embed.addField(
                                translateService.getString("stats.label.threadCount"),
                                translateService.getFormattedString("stats.threadCount", ManagementFactory.getThreadMXBean().getThreadCount()), true);
                        embed.addField(
                                translateService.getString("stats.label.uptime"),
                                translateService.getFormattedString("stats.uptime",
                                    Duration.between(startTime, Instant.now()).getSeconds(),
                                    ManagementFactory.getRuntimeMXBean().getUptime() / 1000), true);
                        embed.addField(
                                translateService.getString("stats.label.memory"),
                                getMemoryText(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()), true);

                        Components components = JSensors.get.components();

                        addComponentStatsToEmbed(components.disks, embed,
                                addComponentStatsToEmbed(components.gpus, embed,
                                        addComponentStatsToEmbed(components.mobos, embed,
                                                addComponentStatsToEmbed(components.cpus, embed, 19))));

                        embed.setTimestamp(Instant.now());
                    });
                }))
                .then();
    }

    private String getMemoryText(MemoryUsage usage) {
        return translateService.getFormattedString("stats.memory",
                usage.getInit() / BYTES_IN_A_MB,
                usage.getCommitted() / BYTES_IN_A_MB,
                usage.getUsed() / BYTES_IN_A_MB,
                usage.getMax() / BYTES_IN_A_MB
                );
    }

    private int addComponentStatsToEmbed(List<? extends Component> components, EmbedCreateSpec spec, int remainingFieldCount) {
        int fieldCount = getFieldCount(components);
        if(CollectionUtils.isNotEmpty(components) && fieldCount < remainingFieldCount) {
            addFields(components, spec);
            return remainingFieldCount - fieldCount;
        } else {
            return remainingFieldCount;
        }
    }

    private int getFieldCount(List<? extends Component> components) {
        int count = 0;
        for(Component c : components) {
            count += 1; //For title
            if(!isComponentEmpty(c)) {
                count += CollectionUtils.size(c.sensors.temperatures) + CollectionUtils.size(c.sensors.fans);
            }
        }
        return count;
    }

    private void addFields(List<? extends Component> components, EmbedCreateSpec spec) {
        for(Component c : components) {
            spec.addField(c.getClass().getSimpleName().toUpperCase(), Objects.toString(c.name, "?"), false);
            if(!isComponentEmpty(c)) {
                c.sensors.temperatures.forEach(t -> {
                    spec.addField(t.name, translateService.getFormattedString("stats.temp", t.value), true);
                });
                c.sensors.fans.forEach(f -> {
                    spec.addField(f.name, translateService.getFormattedString("stats.fan", f.value), true);
                });
            }
        }
    }

    private boolean isComponentEmpty(Component c) {
        return CollectionUtils.isEmpty(c.sensors.temperatures) && CollectionUtils.isEmpty(c.sensors.fans);
    }
}

