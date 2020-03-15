package com.github.lucbui.magic.util;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for common Discord functions
 */
public class DiscordUtils {
    private static final Pattern MENTION_PATTERN = Pattern.compile("<@!([0-9]+)>");

    /**
     * Respond to a message asynchronously.
     * @param message The message to reply to.
     * @param response The response to send.
     * @return A Mono which completes when the response is sent.
     */
    public static Mono<Void> respond(Message message, String response) {
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(response))
                .then();
    }

    /**
     * Get an ID from encoded mention text
     * @param mention The mention to parse
     * @return An Optional containing the ID, or empty if the provided mention was unparseable.
     */
    public static Optional<String> getIdFromMention(String mention) {
        Matcher matcher = MENTION_PATTERN.matcher(mention);
        if(matcher.matches()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get mention text from an ID
     * @param id The ID to use
     * @return The formatted mention text
     */
    public static String getMentionFromId(Snowflake id) {
        return "<@!" + id.asString() + ">";
    }

    public static boolean isValidSnowflake(String test) {
        if(test == null){
            return false;
        }
        try {
            Snowflake.of(test);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
