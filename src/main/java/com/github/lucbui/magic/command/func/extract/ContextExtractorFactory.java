package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;

public class ContextExtractorFactory implements ExtractorFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(ContextExtractorFactory.class);

    @Override
    public Extractor getExtractorFor(Parameter parameter) {
        if(parameter.getType().equals(MessageCreateEvent.class)) {
            LOGGER.warn("Using the old method of MessageCreateEvent. Use CommandUseContext instead");
            return ctx -> Mono.just(((DiscordCommandUseContext)ctx).getEvent()).cast(Object.class);
        } else if(parameter.getType().equals(DiscordCommandUseContext.class)) {
            return ctx -> Mono.just((DiscordCommandUseContext)ctx).cast(Object.class);
        } else {
            return ctx -> Mono.just(ctx).cast(Object.class);
        }
    }
}
