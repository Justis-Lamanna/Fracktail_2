package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.CommandParams;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.execution.BCommand;
import com.github.lucbui.magic.command.execution.ComplexBotMessageBehavior;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandFromMethodParserFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandFromMethodParser.class);

    private final CommandBank commandBank;
    private final List<BotCommandPostProcessor> botCommandPostProcessors;
    private final List<ParameterExtractor<CommandUseContext>> parameterExtractors;

    public CommandFromMethodParserFactory(CommandBank commandBank, List<BotCommandPostProcessor> botCommandPostProcessors, List<ParameterExtractor<CommandUseContext>> parameterExtractors) {
        this.commandBank = commandBank;
        this.botCommandPostProcessors = botCommandPostProcessors;
        this.parameterExtractors = parameterExtractors;
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
            LOGGER.debug("Found command in method " + method.getName());
            ReflectionUtils.makeAccessible(method);
            validateMethod(method);

            String name = getName(method);
            String[] aliases = getAliases(method);
            ComplexBotMessageBehavior behavior = getBehavior(method);
            Optional<BCommand> oldCommandOpt = commandBank.getCommandById(name);
            if(oldCommandOpt.isPresent()) {
                BCommand oldCommand = oldCommandOpt.get();
                behavior.orElse(oldCommand.getBehavior());
                oldCommand.setBehavior(behavior);
            } else {
                commandBank.addCommand(new BCommand(name, aliases, behavior, createCommandPredicate(method)));
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

        private String[] getAliases(Method method) {
            Command cmdAnnotation = method.getAnnotation(Command.class);
            return cmdAnnotation.aliases();
        }

        /**
         * Get the behavior of the command from the method
         *
         * @param method The method to get the behavior of
         * @return The behavior the command exhibits
         */
        protected ComplexBotMessageBehavior getBehavior(Method method) {
            List<Function<CommandUseContext, Mono<Object>>> extractors = getExtractorsFor(method);
            Invoker<CommandUseContext, Object[], Mono<Boolean>> invoker = getInvokerFor(method);
            BotMessageBehavior behavior = (tokens, ctx) -> extractors.stream()
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

            return new ComplexBotMessageBehavior(createBehaviorPredicate(method), behavior);
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

        protected Predicate<CommandUseContext> createCommandPredicate(Method method) {
            return c -> true;
        }

        protected BiPredicate<Tokens, CommandUseContext> createBehaviorPredicate(Method method) {
            BiPredicate<Tokens, CommandUseContext> identity = (t, c) -> true;
            if(method.isAnnotationPresent(CommandParams.class)) {
                identity = identity.and(getCommandParamsPredicate(method.getAnnotation(CommandParams.class)));
            }
            return identity;
        }

        private BiPredicate<? super Tokens, ? super CommandUseContext> getCommandParamsPredicate(CommandParams annotation) {
            switch (annotation.comparison()) {
                case EXACTLY: return (tokens, ctx) -> tokens.getParams().length == annotation.value();
                case OR_LESS: return (tokens, ctx) -> tokens.getParams().length <= annotation.value();
                case OR_MORE: return (tokens, ctx) -> tokens.getParams().length >= annotation.value();
                default: throw new RuntimeException("What?");
            }
        }
    }
}
