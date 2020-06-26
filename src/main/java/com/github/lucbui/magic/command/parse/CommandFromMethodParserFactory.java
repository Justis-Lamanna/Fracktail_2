package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.command.execution.BotCommand;
import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.execution.ComplexBotMessageBehavior;
import com.github.lucbui.magic.command.func.BotCommandProcessor;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.func.extract.Extractor;
import com.github.lucbui.magic.command.func.extract.ExtractorFactory;
import com.github.lucbui.magic.command.func.invoke.Invoker;
import com.github.lucbui.magic.command.func.invoke.InvokerFactory;
import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;
import com.github.lucbui.magic.command.parse.predicate.creator.CommandPredicateFactory;
import com.github.lucbui.magic.exception.BotException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandFromMethodParserFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandFromMethodParser.class);

    private final CommandBank commandBank;
    private final List<BotCommandProcessor> botCommandProcessors;
    private final ExtractorFactory extractorFactory;
    private final InvokerFactory invokerFactory;
    private final CommandPredicateFactory commandPredicateFactory;

    public CommandFromMethodParserFactory(
            CommandBank commandBank,
            List<BotCommandProcessor> botCommandProcessors,
            ExtractorFactory extractorFactory,
            InvokerFactory invokerFactory,
            CommandPredicateFactory commandPredicateFactory) {
        this.commandBank = commandBank;
        this.botCommandProcessors = botCommandProcessors;
        this.extractorFactory = extractorFactory;
        this.invokerFactory = invokerFactory;
        this.commandPredicateFactory = commandPredicateFactory;
    }

    public CommandFromMethodParser get(Object bean) {
        return new CommandFromMethodParser(bean);
    }

    public class CommandFromMethodParser implements ReflectionUtils.MethodCallback {
        private final Object bean;

        public CommandFromMethodParser(Object bean) {
            this.bean = bean;
        }

        @Override
        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
            LOGGER.debug("Found command in method {}.{}", method.getDeclaringClass().getCanonicalName(), method.getName());
            ReflectionUtils.makeAccessible(method);
            validateMethod(method);

            String name = getName(method);
            String[] aliases = getAliases(method);
            LOGGER.debug("+- Command names: {}, aliases: {}", name, aliases);
            BotMessageBehavior behavior = getBehavior(method);
            Optional<BotCommand> oldCommandOpt = commandBank.getCommandById(name);
            CommandPredicate predicate = commandPredicateFactory.createCommandPredicate(method);
            if(oldCommandOpt.isPresent()) {
                LOGGER.debug("\\- Updating command: " + name);
                BotMessageBehavior combined = mergeBehavior(oldCommandOpt.get(), behavior, predicate);
                BotCommand newCommand = new BotCommand(name, aliases, combined);
                commandBank.updateCommand(newCommand);
            } else {
                LOGGER.debug("\\- Creating command: " + name);
                BotCommand newCommand = new BotCommand(name, aliases, new ComplexBotMessageBehavior(predicate, behavior));
                commandBank.addCommand(newCommand);
            }
        }

        protected BotMessageBehavior mergeBehavior(BotCommand botCommand, BotMessageBehavior behavior, CommandPredicate predicate) {
            ComplexBotMessageBehavior b = new ComplexBotMessageBehavior(predicate, behavior);
            b.orElse(botCommand.getBehavior());
            return b;
        }

        /**
         * Validate a method for use as a BotCommand
         *
         * @param method The method to validate
         */
        protected void validateMethod(Method method) {}

        /**
         * Get the names of a command from the method
         *
         * @param method The method to get the command names from
         * @return The command names
         */
        protected String getName(Method method) {
            Command cmdAnnotation = method.getAnnotation(Command.class);
            if (StringUtils.isEmpty(cmdAnnotation.value())) {
                return method.getName().toLowerCase();
            } else {
                return cmdAnnotation.value().toLowerCase();
            }
        }

        protected String[] getAliases(Method method) {
            Command cmdAnnotation = method.getAnnotation(Command.class);
            return cmdAnnotation.aliases();
        }

        /**
         * Get the behavior of the command from the method
         *
         * @param method The method to get the behavior of
         * @return The behavior the command exhibits
         */
        protected BotMessageBehavior getBehavior(Method method) {
            List<Extractor> extractors = getExtractorsFor(method);
            Invoker invoker = invokerFactory.getInvokerFor(bean, method);
            return (tokens, ctx) -> extractors.stream()
                    .map(extractor -> extractor.extract(ctx))
                    .map(mono -> mono.map(Optional::ofNullable).defaultIfEmpty(Optional.empty()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Flux::concat))
                    .collectList()
                    .flatMap(params -> {
                        try {
                            return invoker.invoke(ctx, params.stream().map(opt -> opt.orElse(null)).toArray());
                        } catch (Exception e) {
                            throw new BotException("Error invoking reflected method", e);
                        }
                    });
        }

        private List<Extractor> getExtractorsFor(Method method) {
            return Arrays.stream(method.getParameters())
                    .map(extractorFactory::getExtractorFor)
                    .collect(Collectors.toList());
        }
    }
}
