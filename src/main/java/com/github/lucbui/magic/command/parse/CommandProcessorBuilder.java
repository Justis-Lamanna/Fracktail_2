package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.func.BotCommandProcessor;
import com.github.lucbui.magic.command.func.extract.DefaultExtractorFactory;
import com.github.lucbui.magic.command.func.extract.ExtractorFactory;
import com.github.lucbui.magic.command.func.invoke.DefaultInvokerFactory;
import com.github.lucbui.magic.command.func.invoke.InvokerFactory;
import com.github.lucbui.magic.command.parse.predicate.creator.ChainCommandPredicateFactory;
import com.github.lucbui.magic.command.parse.predicate.creator.CommandParamsPredicateLink;
import com.github.lucbui.magic.command.parse.predicate.creator.CommandPredicateFactory;
import com.github.lucbui.magic.command.parse.predicate.creator.PermissionsPredicateLink;
import com.github.lucbui.magic.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessorBuilder {
    private CommandBank commandBank;
    private Tokenizer tokenizer;
    private List<BotCommandProcessor> botCommandProcessors = new ArrayList<>();
    private ExtractorFactory extractorFactory;
    private InvokerFactory invokerFactory;
    private CommandPredicateFactory commandPredicateFactory;

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

    public CommandProcessorBuilder withDefaultParameterExtractor() {
        extractorFactory = new DefaultExtractorFactory(tokenizer);
        return this;
    }

    public CommandProcessorBuilder withParameterExtractor(ExtractorFactory extractorFactory) {
        this.extractorFactory = extractorFactory;
        return this;
    }

    public CommandProcessorBuilder withDefaultMethodInvoker() {
        invokerFactory = new DefaultInvokerFactory();
        return this;
    }

    public CommandProcessorBuilder withMethodInvoker(InvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
        return this;
    }

    public CommandProcessorBuilder withDefaultCommandPredicateFactory() {
        commandPredicateFactory = new ChainCommandPredicateFactory(
                new CommandParamsPredicateLink(),
                new PermissionsPredicateLink());
        return this;
    }

    public CommandProcessorBuilder withCommandPredicateFactory(CommandPredicateFactory commandPredicateFactory) {
        this.commandPredicateFactory = commandPredicateFactory;
        return this;
    }

    public CommandAnnotationProcessor build() {
        if(extractorFactory == null) {
            withDefaultParameterExtractor();
        }
        if(invokerFactory == null) {
            withDefaultMethodInvoker();
        }
        if(commandPredicateFactory == null) {
            withDefaultCommandPredicateFactory();
        }
        return new CommandAnnotationProcessor(new CommandFromMethodParserFactory(commandBank, botCommandProcessors, extractorFactory, invokerFactory, commandPredicateFactory));
    }
}
