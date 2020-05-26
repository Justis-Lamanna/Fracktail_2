package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Message;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class MessageAnnotationParameterExtractor implements ParameterExtractor<CommandUseContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAnnotationParameterExtractor.class);
    private Tokenizer tokenizer;

    public MessageAnnotationParameterExtractor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.isAnnotationPresent(Message.class);
    }

    @Override
    public <OUT> Function<CommandUseContext, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        if(parameter.getType().equals(discord4j.core.object.entity.Message.class)){
            LOGGER.warn("Using old way with Message, please migrate");
            return ctx -> {
                if(ctx instanceof DiscordCommandUseContext) {
                    return Mono.just(((DiscordCommandUseContext)ctx).getEvent().getMessage()).cast(out);
                } else {
                    return Mono.error(new BotException("Using Message in non-Discord command context"));
                }
            };
        } else if(parameter.getType().equals(String.class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).map(Tokens::getFull).cast(out);
        } else if(parameter.getType().equals(String[].class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).map(tokens ->
                    Stream.concat(Stream.of(tokens.getCommand()), Arrays.stream(tokens.getParams())).toArray(String[]::new))
                    .cast(out);
        }
        throw new IllegalArgumentException("@Message must annotate Message, String, or String[] value");
    }
}
