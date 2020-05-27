package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Discord;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public class MessageCreateEventParameterExtractor implements ParameterExtractor<CommandUseContext> {
    private static Logger LOGGER = LoggerFactory.getLogger(MessageCreateEventParameterExtractor.class);

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.getType().equals(MessageCreateEvent.class) ||
                parameter.getType().equals(CommandUseContext.class) ||
                (parameter.isAnnotationPresent(Discord.class) && parameter.getType().equals(DiscordCommandUseContext.class));
    }

    @Override
    public <OUT> Function<CommandUseContext, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        if(parameter.getType().equals(MessageCreateEvent.class)) {
            LOGGER.warn("Using the old method of MessageCreateEvent. Use CommandUseContext instead");
            return ctx -> Mono.just(((DiscordCommandUseContext)ctx).getEvent()).cast(out);
        } else if(parameter.isAnnotationPresent(Discord.class) && parameter.getType().equals(DiscordCommandUseContext.class)) {
            return ctx -> Mono.just((DiscordCommandUseContext)ctx).cast(out);
        } else {
            return ctx -> Mono.just(ctx).cast(out);
        }
    }
}
