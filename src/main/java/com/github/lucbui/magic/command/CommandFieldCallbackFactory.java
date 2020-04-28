package com.github.lucbui.magic.command;

import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.func.*;
import com.github.lucbui.magic.command.func.extract.ParameterExtractor;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A factory which creates a CommandFieldCallback for each processed bean
 */
public class CommandFieldCallbackFactory {
    private final CommandList commands;
    private final Tokenizer tokenizer;
    private final List<BotCommandPostProcessor> botCommandPostProcessors;
    private final List<ParameterExtractor<MessageCreateEvent>> parameterExtractors;

    public CommandFieldCallbackFactory(CommandList commands,
                                       Tokenizer tokenizer,
                                       List<BotCommandPostProcessor> botCommandPostProcessors,
                                       List<ParameterExtractor<MessageCreateEvent>> parameterExtractors) {
        this.commands = commands;
        this.tokenizer = tokenizer;
        this.botCommandPostProcessors = botCommandPostProcessors;
        this.parameterExtractors = parameterExtractors;
    }

    public CommandFieldCallback getCommandFieldCallback(Object bean) {
        return new CommandFieldCallback(bean);
    }

    private Optional<Tokens> tokenize(discord4j.core.object.entity.Message message) {
        return message.getContent().filter(tokenizer::isValid).map(tokenizer::tokenize);
    }

    public class CommandFieldCallback implements ReflectionUtils.MethodCallback {
        private final Logger LOGGER = LoggerFactory.getLogger(CommandFieldCallback.class);

        private final Object bean;

        /**
         * Initializes the CommandFieldCallback
         *
         * @param bean The bean being processed
         */
        public CommandFieldCallback(Object bean) {
            this.bean = bean;
        }

        @Override
        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
            LOGGER.debug("Found command in method " + method.getName());
            ReflectionUtils.makeAccessible(method);
            validateMethod(method);
            commands.addCommand(createBotCommand(method));
        }

        /**
         * Create a BotCommand from a Method
         *
         * @param method The Method to make a command
         * @return The created command
         */
        protected BotCommand createBotCommand(Method method) {
            BotCommand botCommand = new BotCommand(getNames(method), getHelpText(method), getBehavior(method), getPermissions(method));
            botCommandPostProcessors.forEach(bcpp -> bcpp.process(method, botCommand));
            return botCommand;
        }

        /**
         * Validate a method for use as a BotCommand
         *
         * @param method The method to validate
         */
        protected void validateMethod(Method method) {
            //TODO
        }

        /**
         * Get the names of a command from the method
         *
         * @param method The method to get the command names from
         * @return The command names
         */
        protected String[] getNames(Method method) {
            Command cmdAnnotation = method.getAnnotation(Command.class);
            if (cmdAnnotation.value().length == 0) {
                return new String[]{method.getName()};
            } else {
                return cmdAnnotation.value();
            }
        }

        /**
         * Get the help text from the method
         *
         * @param method The method to get the help text of
         * @return The help text
         */
        protected String getHelpText(Method method) {
            Command cmdAnnotation = method.getAnnotation(Command.class);
            if(StringUtils.isEmpty(cmdAnnotation.help())){
                return getNames(method)[0] + ".help";
            }
            return cmdAnnotation.help();
        }

        /**
         * Get the behavior of the command from the method
         *
         * @param method The method to get the behavior of
         * @return The behavior the command exhibits
         */
        protected BotMessageBehavior getBehavior(Method method) {
            List<Function<MessageCreateEvent, Mono<Object>>> extractors = getExtractorsFor(method);
            Invoker<MessageCreateEvent, Object[], Mono<Void>> invoker = getInvokerFor(method);
            return event -> extractors.stream()
                    .map(func -> func.apply(event))
                    .map(mono -> mono.map(Optional::ofNullable).defaultIfEmpty(Optional.empty()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Flux::concat))
                    .collectList()
                    .flatMap(params -> {
                        try {
                            return invoker.invoke(event, params.stream().map(opt -> opt.orElse(null)).toArray());
                        } catch (Exception e) {
                            throw new BotException("Error invoking reflected method", e);
                        }
                    });
        }

        /**
         * Get the permissions of a command from the method
         *
         * @param method The method to get the permissions of
         * @return The permissions the command requires
         */
        protected PermissionsPredicate getPermissions(Method method) {
            if (method.isAnnotationPresent(Permissions.class)) {
                return Arrays.stream(method.getDeclaredAnnotationsByType(Permissions.class))
                        .map(Permissions::value)
                        .map(permissions -> Collections.unmodifiableSet(Arrays.stream(permissions).collect(Collectors.toSet())))
                        .collect(Collectors.collectingAndThen(Collectors.toList(), ComplexPermissionsPredicate::new));
            }
            return PermissionsPredicate.allPermitted();
        }

        private List<Function<MessageCreateEvent, Mono<Object>>> getExtractorsFor(Method method) {
            return Arrays.stream(method.getParameters()).map(this::getExtractorFor).collect(Collectors.toList());
        }

        private Function<MessageCreateEvent, Mono<Object>> getExtractorFor(Parameter param) {
            return parameterExtractors.stream()
                    .filter(extractor -> extractor.isValidFor(param))
                    .findFirst()
                    .map(extractor -> extractor.getExtractorFor(param, Object.class))
                    .orElseThrow(() -> new BotException("No Parameter Extractor found for parameter " + param));
        }

        private Invoker<MessageCreateEvent, Object[], Mono<Void>> getInvokerFor(Method method) {
            if (method.getReturnType() == null) {
                return getNoReturnInvoker(method);
            } else if (method.getReturnType().equals(String.class)) {
                return getStringReturnInvoker(method);
            } else if (method.getReturnType().equals(Mono.class)) {
                return getMonoReturnInvoker(method);
            } else if (method.getReturnType().equals(Flux.class)) {
                return getFluxReturnInvoker(method);
            } else {
                return getOtherReturnInvoker(method);
            }
        }

        protected Invoker<MessageCreateEvent, Object[], Mono<Void>> getNoReturnInvoker(Method method) {
            return (event, params) -> {
                method.invoke(bean, params);
                return Mono.empty();
            };
        }

        protected Invoker<MessageCreateEvent, Object[], Mono<Void>> getStringReturnInvoker(Method method) {
            return (event, params) -> {
                String response = (String) method.invoke(bean, params);
                return response == null ? Mono.empty() : DiscordUtils.respond(event.getMessage(), response);
            };
        }

        protected Invoker<MessageCreateEvent, Object[], Mono<Void>> getMonoReturnInvoker(Method method) {
            return (event, params) -> {
                Mono<?> response = (Mono<?>) method.invoke(bean, params);
                if (response == null) {
                    return Mono.empty();
                }
                return response.flatMap(res -> {
                    if (res instanceof String) {
                        return DiscordUtils.respond(event.getMessage(), (String) res);
                    } else {
                        return Mono.empty();
                    }
                });
            };
        }

        protected Invoker<MessageCreateEvent, Object[], Mono<Void>> getFluxReturnInvoker(Method method) {
            return (event, params) -> {
                Flux<?> response = (Flux<?>) method.invoke(bean, params);
                if (response == null) {
                    return Mono.empty();
                }
                return response.filter(Objects::nonNull)
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"))
                        .flatMap(msg -> DiscordUtils.respond(event.getMessage(), msg));
            };
        }

        protected Invoker<MessageCreateEvent, Object[], Mono<Void>> getOtherReturnInvoker(Method method) {
            throw new BotException("Return is unexpected type, expected is String, Mono, Flux, or none.");
        }
    }

    public interface Invoker<I1, I2, O> {
        O invoke(I1 in1, I2 in2) throws Exception;
    }
}
