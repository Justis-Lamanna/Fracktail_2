package com.github.lucbui.magic.command.context;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

public abstract class CommandUseContext {
    private String userId;
    private String channelId;

    public CommandUseContext(String userId, String channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public UserIdAndUsername getUserIdAndName() {
        return new UserIdAndUsername(getUserId(), getUsername());
    }

    public String getChannelId() {
        return channelId;
    }

    public abstract Mono<Void> respond(String response);

    public abstract String getUsername();

    public abstract String getMessage();
}
