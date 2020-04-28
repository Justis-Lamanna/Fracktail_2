package com.github.lucbui.magic.command;

import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.command.func.extract.*;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.token.Tokenizer;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandProcessorBuilder {
    public CommandList commandList = CommandList.caseSensitive();
    public Tokenizer tokenizer;
    private List<BotCommandPostProcessor> botCommandPostProcessors = new ArrayList<>();
    private List<ParameterExtractor<MessageCreateEvent>> parameterExtractors = new ArrayList<>();

    public CommandProcessorBuilder(Tokenizer tokenizer, CommandList commandList) {
        this.tokenizer = tokenizer;
        this.commandList = commandList;
    }

    public CommandProcessorBuilder withCommandList(CommandList commandList) {
        this.commandList = commandList;
        return this;
    }

    public CommandProcessorBuilder withTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public CommandProcessorBuilder withBotCommandPostProcessor(BotCommandPostProcessor botCommandPostProcessor) {
        this.botCommandPostProcessors.add(botCommandPostProcessor);
        return this;
    }

    public CommandProcessorBuilder withBotCommandPostProcessors(List<BotCommandPostProcessor> botCommandPostProcessors) {
        this.botCommandPostProcessors.addAll(botCommandPostProcessors);
        return this;
    }

    public CommandProcessorBuilder withDefaultParameterExtractors() {
        this.parameterExtractors.add(new MessageCreateEventParameterExtractor());
        this.parameterExtractors.add(new MessageAnnotationParameterExtractor(tokenizer));
        this.parameterExtractors.add(new ParamsAnnotationParameterExtractor(tokenizer));
        this.parameterExtractors.add(new ParamAnnotationParameterExtractor(tokenizer));
        this.parameterExtractors.add(new UserParameterExtractor());
        return this;
    }

    public CommandProcessorBuilder withParameterExtractor(ParameterExtractor<MessageCreateEvent> parameterExtractor) {
        this.parameterExtractors.add(parameterExtractor);
        return this;
    }

    public CommandAnnotationProcessor build() {
        return new CommandAnnotationProcessor(new CommandFieldCallbackFactory(commandList, tokenizer, botCommandPostProcessors, parameterExtractors));
    }
}
