package com.github.lucbui.bot.services.channel;

import discord4j.core.object.entity.PrivateChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A service used for grabbing bot-related channels.
 */
public interface BotChannelService {
    /**
     * Get the channel for announcements made by the bot.
     * @param guildId The Guild ID of the server
     * @return The TextChannel for making announcements
     */
    Mono<TextChannel> getAnnouncementChannelFor(Snowflake guildId);

    /**
     * Get all announcement channels registered to the bot.
     * This is very powerful, so use with caution.
     * @return All TextChannels for making a global announcement
     */
    Flux<TextChannel> getAllAnnouncementChannels();

    /**
     * Get the DM channel for the admin (me)
     * @return A channel to DM the admin.
     */
    Mono<PrivateChannel> getAdminDMChannel();
}
