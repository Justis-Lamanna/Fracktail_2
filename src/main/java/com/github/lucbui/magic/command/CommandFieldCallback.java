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
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.lucbui.magic.command.func.ParameterExtractor.ofType;

public class CommandFieldCallback implements ReflectionUtils.MethodCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandFieldCallback.class);

    private final Object bean;
    private final CommandList commands;
    private final Tokenizer tokenizer;

    private final ParameterExtractor<discord4j.core.object.entity.Message> messageExtractor;
    private final ParameterExtractor<Optional<Tokens>> parametersExtractor;
    private final ParameterExtractor<Optional<String>> parameterExtractor;
    private final ParameterExtractor<Optional<User>> userExtractor;
    private final ParameterExtractor<Optional<Member>> memberExtractor;

    public CommandFieldCallback(CommandList commands, Tokenizer tokenizer, Object bean) {
        this.commands = commands;
        this.tokenizer = tokenizer;
        this.bean = bean;

        this.messageExtractor = getMessageExtractor();
        this.parametersExtractor = getParametersExtractor();
        this.parameterExtractor = getParameterExtractor();
        this.userExtractor = getUserExtractor();
        this.memberExtractor = getMemberExtractor();
    }

    protected ParameterExtractor<discord4j.core.object.entity.Message> getMessageExtractor() {
        return new ParameterExtractor.Builder<discord4j.core.object.entity.Message>()
                .with(ofType(String.class), msg -> tokenize(msg).map(Tokens::getFull).orElse(null))
                .with(ofType(String[].class), msg -> tokenize(msg).map(CommandFieldCallback::getFullMessageAsArray).orElse(null))
                .with(ofType(discord4j.core.object.entity.Message.class), Function.identity())
                .build();
    }

    protected ParameterExtractor<Optional<Tokens>> getParametersExtractor() {
        return new ParameterExtractor.Builder<Optional<Tokens>>()
                .with(ofType(String.class), tokens -> tokens.map(Tokens::getParamString).orElse(null))
                .with(ofType(String[].class), tokens -> tokens.map(Tokens::getParams).orElse(null))
                .build();
    }

    protected ParameterExtractor<Optional<String>> getParameterExtractor() {
        return new ParameterExtractor.Builder<Optional<String>>()
                .with(ofType(String.class), param -> param.orElse(null))
                .with(ofType(OptionalInt.class), param -> param.map(str -> {
                    try { return OptionalInt.of(Integer.parseInt(str)); }
                    catch (NumberFormatException ex) { return OptionalInt.empty(); }
                }).orElse(OptionalInt.empty()))
                .with(ofType(OptionalLong.class), param -> param.map(str -> {
                    try { return OptionalLong.of(Long.parseLong(str)); }
                    catch (NumberFormatException ex) { return OptionalLong.empty(); }
                }).orElse(OptionalLong.empty()))
                .with(ofType(OptionalDouble.class), param -> param.map(str -> {
                    try { return OptionalDouble.of(Double.parseDouble(str)); }
                    catch (NumberFormatException ex) { return OptionalDouble.empty(); }
                }).orElse(OptionalDouble.empty()))
                .build();
    }

    protected ParameterExtractor<Optional<User>> getUserExtractor() {
        return new ParameterExtractor.Builder<Optional<User>>()
                .with(ofType(String.class), user -> user.map(User::getUsername).orElse(null))
                .with(ofType(User.class), user -> user.orElse(null))
                .build();
    }

    protected ParameterExtractor<Optional<Member>> getMemberExtractor() {
        return new ParameterExtractor.Builder<Optional<Member>>()
                .with(ofType(Member.class), member -> member.orElse(null))
                .build();
    }

    @Override
    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
        LOGGER.debug("Found command in method " + method.getName());
        ReflectionUtils.makeAccessible(method);
        validateMethod(method);
        commands.addCommand(createBotCommand(method));
    }

    protected BotCommand createBotCommand(Method method) {
        return new BotCommand(getNames(method), getHelpText(method), getBehavior(method), getPermissions(method), getTimeout(method));
    }

    protected void validateMethod(Method method) {
        //TODO
    }

    protected String[] getNames(Method method) {
        Command cmdAnnotation = method.getAnnotation(Command.class);
        if(cmdAnnotation.value().length == 0) {
            return new String[]{method.getName()};
        } else {
            return cmdAnnotation.value();
        }
    }

    protected String getHelpText(Method method) {
        Command cmdAnnotation = method.getAnnotation(Command.class);
        return cmdAnnotation.help();
    }

    protected BotMessageBehavior getBehavior(Method method) {
        List<Function<MessageCreateEvent, Object>> extractors = getExtractorsFor(method);
        Invoker<MessageCreateEvent, Object[], Mono<Void>> invoker = getInvokerFor(method);
        return event -> {
            Object[] parameters = extractors.stream()
                    .map(func -> func.apply(event))
                    .toArray();
            try{
                return invoker.invoke(event, parameters);
            } catch (Exception e) {
                throw new BotException("Error invoking reflected method", e);
            }
        };
    }

    protected Set<String> getPermissions(Method method) {
        if(method.isAnnotationPresent(Permissions.class)){
            String[] permissions = method.getAnnotation(Permissions.class).value();
            return Collections.unmodifiableSet(Arrays.stream(permissions).collect(Collectors.toSet()));
        }
        return Collections.emptySet();
    }

    protected Duration getTimeout(Method method) {
        if(method.isAnnotationPresent(Timeout.class)) {
            Timeout timeout = method.getAnnotation(Timeout.class);
            if(timeout.value() > 0) {
                return Duration.of(timeout.value(), timeout.unit());
            }
        }
        return null;
    }

    private List<Function<MessageCreateEvent, Object>> getExtractorsFor(Method method) {
        return Arrays.stream(method.getParameters()).map(this::getExtractorFor).collect(Collectors.toList());
    }

    private Function<MessageCreateEvent, Object> getExtractorFor(Parameter param) {
        if(param.isAnnotationPresent(Message.class)){
            return getMessageExtractor(param);
        } else if(param.isAnnotationPresent(Params.class)){
            return getParametersExtractor(param);
        } else if(param.isAnnotationPresent(Param.class)){
            return getParameterExtractor(param);
        } else if(param.isAnnotationPresent(BasicSender.class)){
            return getUserExtractor(param);
        } else if(param.isAnnotationPresent(Sender.class)){
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
        if(idx < 0){
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
        if(method.getReturnType() == null) {
            return (event, params) -> {
                method.invoke(bean, params);
                return Mono.empty();
            };
        } else if(method.getReturnType().equals(String.class)) {
            return (event, params) -> {
                String response = (String) method.invoke(bean, params);
                return response == null ? Mono.empty() : DiscordUtils.respond(event.getMessage(), response);
            };
        } else if(method.getReturnType().equals(Mono.class)) {
            return (event, params) -> {
                Mono<?> response = (Mono<?>) method.invoke(bean, params);
                return response == null ? Mono.empty() : response.then();
            };
        } else {
            throw new BotException("Return is unexpected type, expected is String, Mono, or none.");
        }
    }

    private Optional<Tokens> tokenize(discord4j.core.object.entity.Message message) {
        return message.getContent().filter(tokenizer::isValid).map(tokenizer::tokenize);
    }

    private static String[] getFullMessageAsArray(Tokens tokens) {
        return Stream.concat(Stream.of(tokens.getCommand()), Arrays.stream(tokens.getParams())).toArray(String[]::new);
    }

    private interface Invoker<I1, I2, O> {
        O invoke(I1 in1, I2 in2) throws Exception;
    }
}
