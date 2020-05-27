package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.command.func.extract.*;
import com.github.lucbui.magic.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessorBuilder {
    public CommandBank commandBank;
    public Tokenizer tokenizer;
    private List<BotCommandPostProcessor> botCommandPostProcessors = new ArrayList<>();
    private List<ParameterExtractor<CommandUseContext>> parameterExtractors = new ArrayList<>();

    public CommandProcessorBuilder(CommandBank commandBank, Tokenizer tokenizer) {
        this.commandBank = commandBank;
        this.tokenizer = tokenizer;
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

    public CommandProcessorBuilder withParameterExtractor(ParameterExtractor<CommandUseContext> parameterExtractor) {
        this.parameterExtractors.add(parameterExtractor);
        return this;
    }

    public CommandAnnotationProcessor build() {
        return new CommandAnnotationProcessor(new CommandFromMethodParserFactory(commandBank, botCommandPostProcessors, parameterExtractors));
    }
}
