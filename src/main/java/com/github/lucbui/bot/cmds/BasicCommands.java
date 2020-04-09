package com.github.lucbui.bot.cmds;

import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.util.Snowflake;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@Commands
public class BasicCommands {
    @Command
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    public String math() {
        return "The answer is 3";
    }

    @Command
    @Permissions("admin")
    public String admin() {
        return "This is a cool command that only admins can use!";
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
    public Mono<Void> whodat(MessageCreateEvent event, @Param(0) String userId) {
        if(!DiscordUtils.isValidSnowflake(userId)) {
            return DiscordUtils.respond(event.getMessage(), "Correct usage: !whodat [user-snowflake]");
        }
        return event.getClient().getUserById(Snowflake.of(userId))
                .map(Optional::of).onErrorReturn(Optional.empty())
                .flatMap(possibleUser -> DiscordUtils.respond(event.getMessage(), possibleUser
                        .map(user -> "They are " + user.getUsername() + ".")
                        .orElse("I have no idea who that is.")));
    }

    @Command
    public String updog() {
        return "No I'm not adding this command.";
    }
}
