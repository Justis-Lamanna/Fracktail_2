package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.command.context.UserIdAndUsername;
import com.github.lucbui.magic.exception.BotException;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;

public class UserAnnotationExtractorFactory implements ExtractorFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(UserAnnotationExtractorFactory.class);

    @Override
    public Extractor getExtractorFor(Parameter parameter) {
        if(parameter.getType().equals(User.class)) {
            LOGGER.warn("Use of old way User. Migrate to a new way");
            return ctx -> {
                if(ctx instanceof DiscordCommandUseContext) {
                    return Mono.justOrEmpty(((DiscordCommandUseContext)ctx).getEvent().getMessage().getAuthor()).cast(Object.class);
                } else {
                    return Mono.error(new BotException("Use of User without a Discord context"));
                }
            };
        } else if(parameter.getType().equals(String.class)) {
            return ctx -> Mono.justOrEmpty(ctx.getUsername()).cast(Object.class);
        } else if(parameter.getType().equals(UserIdAndUsername.class)) {
            return ctx -> Mono.justOrEmpty(ctx.getUserIdAndName()).cast(Object.class);
        }
        throw new IllegalArgumentException("@BasicSender must annotate String or User value");
    }
}
