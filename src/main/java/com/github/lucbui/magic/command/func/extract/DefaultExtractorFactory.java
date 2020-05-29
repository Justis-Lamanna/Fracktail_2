package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.BasicSender;
import com.github.lucbui.magic.annotation.Message;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.annotation.Params;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.lang.reflect.Parameter;

public class DefaultExtractorFactory implements ExtractorFactory {
    private final ParamAnnotationExtractorFactory paramFactory;
    private final ParamsAnnotationExtractorFactory paramsFactory;
    private final UserAnnotationExtractorFactory userFactory;
    private final MessageAnnotationExtractorFactory messageFactory;
    private final ContextExtractorFactory contextFactory;

    public DefaultExtractorFactory(Tokenizer tokenizer) {
        paramFactory = new ParamAnnotationExtractorFactory(tokenizer);
        paramsFactory = new ParamsAnnotationExtractorFactory(tokenizer);
        userFactory = new UserAnnotationExtractorFactory();
        messageFactory = new MessageAnnotationExtractorFactory(tokenizer);
        contextFactory = new ContextExtractorFactory();
    }

    @Override
    public Extractor getExtractorFor(Parameter parameter) {
        if(parameter.getType().equals(MessageCreateEvent.class) ||
                parameter.getType().equals(CommandUseContext.class) ||
                parameter.getType().equals(DiscordCommandUseContext.class)) {
            return contextFactory.getExtractorFor(parameter);
        } else if(parameter.isAnnotationPresent(Param.class)) {
            return paramFactory.getExtractorFor(parameter);
        } else if(parameter.isAnnotationPresent(Params.class)) {
            return paramsFactory.getExtractorFor(parameter);
        } else if(parameter.isAnnotationPresent(BasicSender.class)) {
            return userFactory.getExtractorFor(parameter);
        } else if(parameter.isAnnotationPresent(Message.class)) {
            return messageFactory.getExtractorFor(parameter);
        }

        throw new BotException("No Parameter Extractor found for parameter " + parameter);
    }
}
