package com.github.lucbui.bot.services.channel;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.PrivateChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BasicBotChannelService implements BotChannelService {
    public static final Snowflake LUCBUILAND_GUILD_ID = Snowflake.of("423976318082744321");
    public static final Snowflake ANNOUNCEMENT_CHANNEL_ID = Snowflake.of("423981792639320064");
    public static final Snowflake LUCBUI_ID = Snowflake.of("248612704019808258");

    @Autowired
    private DiscordClient bot;

    @Override
    public Mono<TextChannel> getAnnouncementChannelFor(Snowflake guildId) {
        if(guildId.equals(LUCBUILAND_GUILD_ID)){
            return bot.getChannelById(ANNOUNCEMENT_CHANNEL_ID)
                    .cast(TextChannel.class);
        }
        return Mono.empty();
    }

    @Override
    public Flux<TextChannel> getAllAnnouncementChannels() {
        return bot.getChannelById(ANNOUNCEMENT_CHANNEL_ID)
                .cast(TextChannel.class)
                .flux();
    }

    @Override
    public Mono<PrivateChannel> getAdminDMChannel() {
        return bot.getUserById(LUCBUI_ID)
                .flatMap(User::getPrivateChannel);
    }
}
