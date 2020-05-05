package com.github.lucbui.magic.command.context;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;

public class CommandUseContext {
    private String userId;
    private String channelId;

    public CommandUseContext(String userId, String channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public static CommandUseContext from(MessageCreateEvent event) {
        return new CommandUseContext(
                event.getMessage().getAuthor().map(User::getId).map(Snowflake::asString).orElse(null),
                event.getGuildId().map(Snowflake::asString).orElse(null));
    }

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }
}
