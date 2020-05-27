package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.execution.BCommand;
import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.execution.ComplexBotMessageBehavior;
import com.github.lucbui.magic.command.func.BotCommandProcessor;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.func.extract.ParameterExtractor;
import com.github.lucbui.magic.command.func.invoke.*;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokens;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandFromMethodParserFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandFromMethodParser.class);

    private final CommandBank commandBank;
    private final List<BotCommandProcessor> botCommandProcessors;
    private final List<ParameterExtractor<CommandUseContext>> parameterExtractors;
    private final BotCommandBehaviorPredicateCreator botCommandBehaviorPredicateCreator;
    private final BotBehaviorMerger botBehaviorMerger;
    private final BotCommandPredicateCreator botCommandPredicateCreator;

    public CommandFromMethodParserFactory(CommandBank commandBank,
                                          List<BotCommandProcessor> botCommandProcessors,
                                          List<ParameterExtractor<CommandUseContext>> parameterExtractors,
                                          BotCommandBehaviorPredicateCreator botCommandBehaviorPredicateCreator,
                                          BotBehaviorMerger botBehaviorMerger,
                                          BotCommandPredicateCreator botCommandPredicateCreator) {
        this.commandBank = commandBank;
        this.botCommandProcessors = botCommandProcessors;
        this.parameterExtractors = parameterExtractors;
        this.botCommandBehaviorPredicateCreator = botCommandBehaviorPredicateCreator;
        this.botBehaviorMerger = botBehaviorMerger;
        this.botCommandPredicateCreator = botCommandPredicateCreator;
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
            BiPredicate<Tokens, CommandUseContext> predicate = botCommandBehaviorPredicateCreator.createBehaviorPredicate(method);
            Optional<BCommand> oldCommandOpt = commandBank.getCommandById(name);
            CommandCreateContext ctx = new CommandCreateContext(name, aliases, behavior);
            if(oldCommandOpt.isPresent()) {
                botCommandProcessors.forEach(bcpp -> bcpp.beforeUpdate(method, ctx));
                LOGGER.debug("\\- Updating command: " + name);
                BCommand oldCommand = oldCommandOpt.get();
                oldCommand.setBehavior(botBehaviorMerger.mergeBehavior(oldCommand.getBehavior(), behavior, predicate));
                botCommandProcessors.forEach(bcpp -> bcpp.afterUpdate(method, oldCommand, ctx));
            } else {
                botCommandProcessors.forEach(bcpp -> bcpp.beforeCreate(method, ctx));
                BCommand command = new BCommand(name, aliases, new ComplexBotMessageBehavior(predicate, behavior), botCommandPredicateCreator.createCommandPredicate(method));
                LOGGER.debug("\\- Creating command: " + name);
                commandBank.addCommand(command);
                botCommandProcessors.forEach(bcpp -> bcpp.afterCreate(method, command, ctx));
            }
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
                return method.getName();
            } else {
                return cmdAnnotation.value();
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
            List<Function<CommandUseContext, Mono<Object>>> extractors = getExtractorsFor(method);
            Invoker<CommandUseContext, Object[], Mono<Boolean>> invoker = getInvokerFor(method);
            return (tokens, ctx) -> extractors.stream()
                    .map(func -> func.apply(ctx))
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

        private List<Function<CommandUseContext, Mono<Object>>> getExtractorsFor(Method method) {
            return Arrays.stream(method.getParameters()).map(this::getExtractorFor).collect(Collectors.toList());
        }

        private Function<CommandUseContext, Mono<Object>> getExtractorFor(Parameter param) {
            return parameterExtractors.stream()
                    .filter(extractor -> extractor.isValidFor(param))
                    .findFirst()
                    .map(extractor -> extractor.getExtractorFor(param, Object.class))
                    .orElseThrow(() -> new BotException("No Parameter Extractor found for parameter " + param));
        }

        protected Invoker<CommandUseContext, Object[], Mono<Boolean>> getInvokerFor(Method method) {
            if (method.getReturnType() == null) {
                return new NoReturnInvoker(bean, method);
            } else if (method.getReturnType().equals(String.class)) {
                return new StringReturnInvoker(bean, method);
            } else if (method.getReturnType().equals(Mono.class)) {
                return new MonoReturnInvoker(bean, method);
            } else if (method.getReturnType().equals(Flux.class)) {
                return new FluxReturnInvoker(bean, method);
            } else {
                throw new BotException("Return is unexpected type, expected is String, Mono, Flux, or none.");
            }
        }
    }
}
