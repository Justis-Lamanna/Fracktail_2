package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.func.BotCommandProcessor;
import com.github.lucbui.magic.command.func.extract.DefaultExtractorFactory;
import com.github.lucbui.magic.command.func.extract.ExtractorFactory;
import com.github.lucbui.magic.command.func.invoke.DefaultInvokerFactory;
import com.github.lucbui.magic.command.func.invoke.InvokerFactory;
import com.github.lucbui.magic.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessorBuilder {
    private CommandBank commandBank;
    private Tokenizer tokenizer;
    private List<BotCommandProcessor> botCommandProcessors = new ArrayList<>();
    private ExtractorFactory extractorFactory;
    private InvokerFactory invokerFactory;

    public CommandProcessorBuilder(CommandBank commandBank, Tokenizer tokenizer) {
        this.commandBank = commandBank;
        this.tokenizer = tokenizer;
    }

    public CommandProcessorBuilder withBotCommandPostProcessor(BotCommandProcessor botCommandProcessor) {
        this.botCommandProcessors.add(botCommandProcessor);
        return this;
    }

    public CommandProcessorBuilder withBotCommandPostProcessors(List<BotCommandProcessor> botCommandProcessors) {
        this.botCommandProcessors.addAll(botCommandProcessors);
        return this;
    }

    public CommandProcessorBuilder withDefaultParameterExtractors() {
        extractorFactory = new DefaultExtractorFactory(tokenizer);
        return this;
    }

    public CommandProcessorBuilder withParameterExtractor(ExtractorFactory extractorFactory) {
        this.extractorFactory = extractorFactory;
        return this;
    }

    public CommandProcessorBuilder withDefaultMethodInvokers() {
        invokerFactory = new DefaultInvokerFactory();
        return this;
    }

    public CommandProcessorBuilder withMethodInvoker(InvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
        return this;
    }

    public CommandAnnotationProcessor build() {
        if(extractorFactory == null) {
            withDefaultParameterExtractors();
        }
        if(invokerFactory == null) {
            withDefaultMethodInvokers();
        }
        return new CommandAnnotationProcessor(new CommandFromMethodParserFactory(commandBank, botCommandProcessors, extractorFactory, invokerFactory));
    }
}
