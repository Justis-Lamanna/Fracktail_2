package com.github.lucbui.magic.command;

import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.func.ParameterExtractor;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
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

import static com.github.lucbui.magic.command.func.ParameterExtractor.ofType;

/**
 * A factory which creates a CommandFieldCallback for each processed bean
 */
public class CommandFieldCallbackFactory {
    private final CommandList commands;
    private final Tokenizer tokenizer;

    public CommandFieldCallbackFactory(CommandList commands, Tokenizer tokenizer) {
        this.commands = commands;
        this.tokenizer = tokenizer;
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

        private final ParameterExtractor<Message> messageExtractor = new ParameterExtractor.Builder<discord4j.core.object.entity.Message>()
                .with(ofType(String.class), msg -> tokenize(msg).map(Tokens::getFull).orElse(null))
                .with(ofType(String[].class), msg -> tokenize(msg).map(this::getFullMessageAsArray).orElse(null))
                .with(ofType(discord4j.core.object.entity.Message.class), Function.identity())
                .build();
        private final ParameterExtractor<Optional<Tokens>> parametersExtractor = new ParameterExtractor.Builder<Optional<Tokens>>()
                .with(ofType(String.class), tokens -> tokens.map(Tokens::getParamString).orElse(null))
                .with(ofType(String[].class), tokens -> tokens.map(Tokens::getParams).orElse(null))
                .build();
        private final ParameterExtractor<Optional<String>> parameterExtractor = new ParameterExtractor.Builder<Optional<String>>()
                .with(ofType(String.class), param -> param.orElse(null))
                .with(ofType(OptionalInt.class), param -> param.map(str -> {
                    try {
                        return OptionalInt.of(Integer.parseInt(str));
                    } catch (NumberFormatException ex) {
                        return OptionalInt.empty();
                    }
                }).orElse(OptionalInt.empty()))
                .with(ofType(OptionalLong.class), param -> param.map(str -> {
                    try {
                        return OptionalLong.of(Long.parseLong(str));
                    } catch (NumberFormatException ex) {
                        return OptionalLong.empty();
                    }
                }).orElse(OptionalLong.empty()))
                .with(ofType(OptionalDouble.class), param -> param.map(str -> {
                    try {
                        return OptionalDouble.of(Double.parseDouble(str));
                    } catch (NumberFormatException ex) {
                        return OptionalDouble.empty();
                    }
                }).orElse(OptionalDouble.empty()))
                .build();
        private final ParameterExtractor<Optional<User>> userExtractor = new ParameterExtractor.Builder<Optional<User>>()
                .with(ofType(String.class), user -> user.map(User::getUsername).orElse(null))
                .with(ofType(User.class), user -> user.orElse(null))
                .build();
        private final ParameterExtractor<Optional<Member>> memberExtractor = new ParameterExtractor.Builder<Optional<Member>>()
                .with(ofType(Member.class), member -> member.orElse(null))
                .build();

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
            return new BotCommand(getNames(method), getHelpText(method), getBehavior(method), getPermissions(method), getTimeout(method));
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
            return cmdAnnotation.help();
        }

        /**
         * Get the behavior of the command from the method
         *
         * @param method The method to get the behavior of
         * @return The behavior the command exhibits
         */
        protected BotMessageBehavior getBehavior(Method method) {
            List<Function<MessageCreateEvent, Object>> extractors = getExtractorsFor(method);
            Invoker<MessageCreateEvent, Object[], Mono<Void>> invoker = getInvokerFor(method);
            return event -> {
                Object[] parameters = extractors.stream()
                        .map(func -> func.apply(event))
                        .toArray();
                try {
                    return invoker.invoke(event, parameters);
                } catch (Exception e) {
                    throw new BotException("Error invoking reflected method", e);
                }
            };
        }

        /**
         * Get the permissions of a command from the method
         *
         * @param method The method to get the permissions of
         * @return The permissions the command requires
         */
        protected Set<String> getPermissions(Method method) {
            if (method.isAnnotationPresent(Permissions.class)) {
                String[] permissions = method.getAnnotation(Permissions.class).value();
                return Collections.unmodifiableSet(Arrays.stream(permissions).collect(Collectors.toSet()));
            }
            return Collections.emptySet();
        }

        /**
         * Get the timeout of a command from the method
         *
         * @param method The method to get the timeout from
         * @return The timeout of the command
         */
        protected Duration getTimeout(Method method) {
            if (method.isAnnotationPresent(Timeout.class)) {
                Timeout timeout = method.getAnnotation(Timeout.class);
                if (timeout.value() > 0) {
                    return Duration.of(timeout.value(), timeout.unit());
                }
            }
            return null;
        }

        private List<Function<MessageCreateEvent, Object>> getExtractorsFor(Method method) {
            return Arrays.stream(method.getParameters()).map(this::getExtractorFor).collect(Collectors.toList());
        }

        private Function<MessageCreateEvent, Object> getExtractorFor(Parameter param) {
            if (param.getType().equals(MessageCreateEvent.class)) {
                return (evt) -> evt;
            } else if (param.isAnnotationPresent(com.github.lucbui.magic.annotation.Message.class)) {
                return getMessageExtractor(param);
            } else if (param.isAnnotationPresent(Params.class)) {
                return getParametersExtractor(param);
            } else if (param.isAnnotationPresent(Param.class)) {
                return getParameterExtractor(param);
            } else if (param.isAnnotationPresent(BasicSender.class)) {
                return getUserExtractor(param);
            } else if (param.isAnnotationPresent(Sender.class)) {
                return getMemberExtractor(param);
            } else {
                throw new IllegalArgumentException("Method contains too many arguments");
            }
        }

        private Function<MessageCreateEvent, Object> getMessageExtractor(Parameter param) {
            Function<discord4j.core.object.entity.Message, ?> extractor = messageExtractor.getExtractor(param);
            return event -> extractor.apply(event.getMessage());
        }

        private Function<MessageCreateEvent, Object> getParametersExtractor(Parameter param) {
            Function<Optional<Tokens>, ?> extractor = parametersExtractor.getExtractor(param);
            return event -> extractor.apply(tokenize(event.getMessage()));
        }

        private Function<MessageCreateEvent, Object> getParameterExtractor(Parameter param) {
            int idx = param.getAnnotation(Param.class).value();
            if (idx < 0) {
                throw new IllegalArgumentException("Parameter class value must be >0");
            }
            Function<Optional<String>, ?> extractor = parameterExtractor.getExtractor(param);
            return event -> {
                Optional<String> paramIfPresent = tokenize(event.getMessage())
                        .map(Tokens::getParams)
                        .filter(params -> idx < params.length)
                        .map(params -> params[idx]);
                return extractor.apply(paramIfPresent);
            };
        }

        private Function<MessageCreateEvent, Object> getUserExtractor(Parameter param) {
            Function<Optional<User>, ?> extractor = userExtractor.getExtractor(param);
            return event -> extractor.apply(event.getMessage().getAuthor());
        }

        private Function<MessageCreateEvent, Object> getMemberExtractor(Parameter param) {
            Function<Optional<Member>, ?> extractor = memberExtractor.getExtractor(param);
            return event -> extractor.apply(event.getMember());
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

        private String[] getFullMessageAsArray(Tokens tokens) {
            return Stream.concat(Stream.of(tokens.getCommand()), Arrays.stream(tokens.getParams())).toArray(String[]::new);
        }
    }

    public interface Invoker<I1, I2, O> {
        O invoke(I1 in1, I2 in2) throws Exception;
    }
}
