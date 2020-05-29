package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Stream;

public class MessageAnnotationExtractorFactory implements ExtractorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAnnotationExtractorFactory.class);
    private Tokenizer tokenizer;

    public MessageAnnotationExtractorFactory(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Extractor getExtractorFor(Parameter parameter) {
        if(parameter.getType().equals(discord4j.core.object.entity.Message.class)){
            LOGGER.warn("Using old way with Message, please migrate");
            return ctx -> {
                if(ctx instanceof DiscordCommandUseContext) {
                    return Mono.just(((DiscordCommandUseContext)ctx).getEvent().getMessage()).cast(Object.class);
                } else {
                    return Mono.error(new BotException("Using Message in non-Discord command context"));
                }
            };
        } else if(parameter.getType().equals(String.class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).map(Tokens::getFull).cast(Object.class);
        } else if(parameter.getType().equals(String[].class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).map(tokens ->
                    Stream.concat(Stream.of(tokens.getCommand()), Arrays.stream(tokens.getParams())).toArray(String[]::new))
                    .cast(Object.class);
        }
        throw new IllegalArgumentException("@Message must annotate Message, String, or String[] value");
    }
}
