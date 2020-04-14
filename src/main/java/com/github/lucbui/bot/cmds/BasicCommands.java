package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Component
@Commands
public class BasicCommands {
    @Autowired
    private TranslateService translateService;

    @Autowired
    private DiscordClient bot;

    @Command
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    public String math() {
        return translateService.getString("math.text");
    }

    @Command
    @Permissions("admin")
    public String admin() {
        return translateService.getString("admin.text");
    }

    @Command
    @Timeout(value = 5, unit = ChronoUnit.MINUTES)
    public String rafo() {
        return "<:rafo1:596138147285434415><:rafo2:596138147797270538><:rafo3:596138379603869697><:rafo4:596138380132089879>\n" +
                "<:rafo5:596138380211781641><:rafo6:596138491469889536><:rafo7:596138588584804373><:rafo8:596138610193858581>\n" +
                "<:rafo9:596138646130917376><:rafo10:596138678108291082><:rafo11:596138697607348257><:rafo12:596138718817943552>\n" +
                "<:rafo13:596138741052211210><:rafo14:596138758160515073><:rafo15:596138771779682315><:rafo16:596138788984586268>";
    }

    @Command
    public Mono<String> whodat(@Param(0) String userId) {
        if(!DiscordUtils.isValidSnowflake(userId)) {
            return Mono.fromSupplier(() -> translateService.getString("whodat.validation.illegalParams"));
        }
        return bot.getUserById(Snowflake.of(userId))
                .map(Optional::of).onErrorReturn(Optional.empty())
                .map(possibleUser -> possibleUser
                        .map(user -> translateService.getFormattedString("whodat.success", user.getUsername()))
                        .orElse(translateService.getString("whodat.failure")));
    }

    @Command
    @Timeout(value = 30)
    public Mono<String> timestampify(@BasicSender User sender, @Param(0) String snowflake) {
        return Mono.justOrEmpty(snowflake)
                .defaultIfEmpty(sender.getId().asString())
                .map(Snowflake::of)
                .map(f -> translateService.getFormattedString("timestampify.success", Date.from(f.getTimestamp())))
                .onErrorResume(ex -> Mono.just(translateService.getString("timestampify.validation.invalidSnowflake")));
    }

    @Command
    public String updog() {
        return translateService.getString("updog.text");
    }
}
