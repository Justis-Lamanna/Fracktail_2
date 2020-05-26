package com.github.lucbui.magic.command.context;

import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

public class DiscordCommandUseContext extends CommandUseContext {
    private final MessageCreateEvent evt;

    public DiscordCommandUseContext(String userId, String channelId, MessageCreateEvent evt) {
        super(userId, channelId);
        this.evt = evt;
    }

    public static DiscordCommandUseContext from(MessageCreateEvent event) {
        return new DiscordCommandUseContext(
                event.getMessage().getAuthor().map(User::getId).map(Snowflake::asString).orElse(null),
                event.getGuildId().map(Snowflake::asString).orElse(null),
                event);
    }

    public MessageCreateEvent getEvent() {
        return evt;
    }

    @Override
    public Mono<Void> respond(String response) {
        return DiscordUtils.respond(evt.getMessage(), response);
    }

    @Override
    public String getUsername() {
        return evt.getMessage().getAuthor().map(User::getUsername).orElse(null);
    }

    @Override
    public String getMessage() {
        return evt.getMessage().getContent().orElse(null);
    }
}
