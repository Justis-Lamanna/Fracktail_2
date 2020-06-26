package com.github.lucbui.magic.util;

import com.github.lucbui.magic.exception.BotException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import discord4j.rest.http.client.ClientException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for common Discord functions
 */
public class DiscordUtils {
    public static final Predicate<? super Throwable> ON_FORBIDDEN =
            ex -> ex instanceof ClientException && ((ClientException)ex).getStatus().code() == 403;

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

    public static boolean isMention(String mention) {
        return MENTION_PATTERN.matcher(mention).matches();
    }

    /**
     * Get mention text from an ID
     * @param id The ID to use
     * @return The formatted mention text
     */
    public static String getMentionFromId(Snowflake id) {
        return "<@!" + id.asString() + ">";
    }

    /**
     * Get mention text from an ID
     * @param id The ID to use
     * @return The formatted mention text
     */
    public static String getRoleMentionFromId(Snowflake id) {
        return "<@&" + id.asString() + ">";
    }

    /**
     * Test if a String can be converted into a Snowflake.
     * @param test The string to test
     * @return True, if the text can be converted safely into a Boolean.
     */
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

    public static Optional<Snowflake> toSnowflakeFromMentionOrLiteral(String mentionOrLiteral) {
        String id = getIdFromMention(mentionOrLiteral).orElse(mentionOrLiteral);
        return isValidSnowflake(id) ? Optional.of(Snowflake.of(id)) : Optional.empty();
    }

    public static boolean isDM(MessageCreateEvent event) {
        return !event.getMessage().getAuthor().isPresent();
    }

    public static String getEmojiString(Reaction reaction) {
        Optional<ReactionEmoji.Unicode> asUnicode = reaction.getEmoji().asUnicodeEmoji();
        Optional<ReactionEmoji.Custom> asCustom = reaction.getEmoji().asCustomEmoji();
        if(asCustom.isPresent()) {
            return getCustomEmojiString(asCustom.get());
        } else if(asUnicode.isPresent()) {
            return asUnicode.get().getRaw();
        } else {
            throw new BotException("Encountered react that was neither custom nor unicode");
        }
    }

    public static String getCustomEmojiString(ReactionEmoji.Custom emoji) {
        if(emoji.isAnimated()) {
            return "<a:" + emoji.getName() + ":" + emoji.getId().asString() + ">";
        } else {
            return "<" + emoji.getName() + ":" + emoji.getId().asString() + ">";
        }
    }
}
