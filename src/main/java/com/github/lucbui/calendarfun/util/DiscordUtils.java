package com.github.lucbui.calendarfun.util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtils {
    private static final Pattern MENTION_PATTERN = Pattern.compile("<@!([0-9]+)>");

    public static Mono<Void> respond(Message message, String response) {
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(response))
                .then();
    }

    public static Optional<String> getIdFromMention(String mention) {
        Matcher matcher = MENTION_PATTERN.matcher(mention);
        if(matcher.matches()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }
}
