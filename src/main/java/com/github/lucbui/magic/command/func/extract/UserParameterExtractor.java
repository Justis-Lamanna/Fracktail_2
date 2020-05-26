package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.BasicSender;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.command.context.UserIdAndUsername;
import com.github.lucbui.magic.exception.BotException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public class UserParameterExtractor implements ParameterExtractor<CommandUseContext> {
    private static Logger LOGGER = LoggerFactory.getLogger(UserParameterExtractor.class);

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.isAnnotationPresent(BasicSender.class);
    }

    @Override
    public <OUT> Function<CommandUseContext, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        BasicSender bs = parameter.getAnnotation(BasicSender.class);
        if(parameter.getType().equals(User.class)) {
            LOGGER.warn("Use of old way User. Migrate to a new way");
            return ctx -> {
                if(ctx instanceof DiscordCommandUseContext) {
                    return Mono.justOrEmpty(((DiscordCommandUseContext)ctx).getEvent().getMessage().getAuthor()).cast(out);
                } else {
                    return Mono.error(new BotException("Use of User without a Discord context"));
                }
            };
        } else if(parameter.getType().equals(String.class)) {
            if(bs.injectId()) {
                return ctx -> Mono.justOrEmpty(ctx.getUserId()).cast(out);
            } else {
                return ctx -> Mono.justOrEmpty(ctx.getUsername()).cast(out);
            }
        } else if(parameter.getType().equals(UserIdAndUsername.class)) {
            return ctx -> Mono.justOrEmpty(ctx.getUserIdAndName()).cast(out);
        }
        throw new IllegalArgumentException("@BasicSender must annotate String or User value");
    }
}
