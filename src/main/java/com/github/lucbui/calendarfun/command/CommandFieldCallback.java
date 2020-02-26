package com.github.lucbui.calendarfun.command;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.annotation.Message;
import com.github.lucbui.calendarfun.annotation.Param;
import com.github.lucbui.calendarfun.annotation.Params;
import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.command.func.BotMessageBehavior;
import com.github.lucbui.calendarfun.command.func.ParameterExtractor;
import com.github.lucbui.calendarfun.exception.BotException;
import com.github.lucbui.calendarfun.token.Tokenizer;
import com.github.lucbui.calendarfun.token.Tokens;
import com.github.lucbui.calendarfun.util.OptionalUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandFieldCallback implements ReflectionUtils.MethodCallback {
    private final Object bean;
    private final CommandStore store;
    private final Tokenizer tokenizer;

    private final ParameterExtractor<discord4j.core.object.entity.Message> messageExtractor;
    private final ParameterExtractor<Optional<Tokens>> parametersExtractor;
    private final ParameterExtractor<Optional<String>> parameterExtractor;

    public CommandFieldCallback(CommandStore store, Tokenizer tokenizer, Object bean) {
        this.store = store;
        this.tokenizer = tokenizer;
        this.bean = bean;

        this.messageExtractor = getMessageExtractor();
        this.parametersExtractor = getParametersExtractor();
        this.parameterExtractor = getParameterExtractor();
    }

    protected ParameterExtractor<discord4j.core.object.entity.Message> getMessageExtractor() {
        return new ParameterExtractor.Builder<discord4j.core.object.entity.Message>()
                .with(ParameterExtractor.ofType(String.class), msg -> tokenize(msg).map(Tokens::getFull).orElse(null))
                .with(ParameterExtractor.ofType(String[].class), msg -> tokenize(msg).map(CommandFieldCallback::getFullMessageAsArray).orElse(null))
                .with(ParameterExtractor.ofType(OptionalUtils.PTR_OPTIONAL_STRING), msg -> tokenize(msg).map(Tokens::getFull))
                .with(ParameterExtractor.ofType(OptionalUtils.PTR_OPTIONAL_STRING_ARRAY), msg -> tokenize(msg).map(CommandFieldCallback::getFullMessageAsArray))
                .with(ParameterExtractor.ofType(discord4j.core.object.entity.Message.class), Function.identity())
                .build();
    }

    protected ParameterExtractor<Optional<Tokens>> getParametersExtractor() {
        return new ParameterExtractor.Builder<Optional<Tokens>>()
                .with(ParameterExtractor.ofType(String.class), tokens -> tokens.map(Tokens::getParamString).orElse(null))
                .with(ParameterExtractor.ofType(String[].class), tokens -> tokens.map(Tokens::getParams).orElse(null))
                .with(ParameterExtractor.ofType(OptionalUtils.PTR_OPTIONAL_STRING), tokens -> tokens.map(Tokens::getParamString))
                .with(ParameterExtractor.ofType(OptionalUtils.PTR_OPTIONAL_STRING_ARRAY), tokens -> tokens.map(Tokens::getParams))
                .build();
    }

    protected ParameterExtractor<Optional<String>> getParameterExtractor() {
        return new ParameterExtractor.Builder<Optional<String>>()
                .with(ParameterExtractor.ofType(String.class), param -> param.orElse(null))
                .with(ParameterExtractor.ofType(OptionalUtils.PTR_OPTIONAL_STRING), Function.identity())
                .with(ParameterExtractor.ofType(OptionalInt.class), param -> param.map(str -> {
                    try { return OptionalInt.of(Integer.parseInt(str)); }
                    catch (NumberFormatException ex) { return OptionalInt.empty(); }
                }).orElse(OptionalInt.empty()))
                .with(ParameterExtractor.ofType(OptionalLong.class), param -> param.map(str -> {
                    try { return OptionalLong.of(Long.parseLong(str)); }
                    catch (NumberFormatException ex) { return OptionalLong.empty(); }
                }).orElse(OptionalLong.empty()))
                .with(ParameterExtractor.ofType(OptionalDouble.class), param -> param.map(str -> {
                    try { return OptionalDouble.of(Double.parseDouble(str)); }
                    catch (NumberFormatException ex) { return OptionalDouble.empty(); }
                }).orElse(OptionalDouble.empty()))
                .build();
    }

    @Override
    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
        if(!method.isAnnotationPresent(Command.class)){
            return;
        }
        System.out.println("Found a method: " + method.getName());
        ReflectionUtils.makeAccessible(method);
        validateMethod(method);
        store.addCommand(new BotCommand(getNames(method), getHelpText(method), getBehavior(method)));
    }

    private void validateMethod(Method method) {
        //TODO
    }

    private String[] getNames(Method method) {
        Command cmdAnnotation = method.getAnnotation(Command.class);
        if(cmdAnnotation.value().length == 0) {
            return new String[]{method.getName()};
        } else {
            return cmdAnnotation.value();
        }
    }

    private String getHelpText(Method method) {
        Command cmdAnnotation = method.getAnnotation(Command.class);
        return cmdAnnotation.help();
    }

    protected BotMessageBehavior getBehavior(Method method) {
        List<Function<MessageCreateEvent, Object>> extractors = getExtractorsFor(method);
        return event -> {
            Object[] parameters = extractors.stream()
                    .map(func -> func.apply(event))
                    .toArray();
            try{
                if(method.getReturnType() == null) {
                    method.invoke(bean, parameters);
                    return Mono.empty();
                }
                Object returnValue = method.invoke(bean, parameters);
                if(returnValue == null){
                    return Mono.empty();
                } else if(returnValue instanceof String) {
                    return event.getMessage()
                            .getChannel()
                            .flatMap(
                                channel -> channel.createMessage((String)returnValue)
                            ).then();
                } else if(returnValue instanceof Mono) {
                    return ((Mono<?>)returnValue).then();
                } else {
                    throw new BotException("Return is unexpected type, expected is String or Mono");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BotException("Error invoking reflected method", e);
            }
        };
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

    private Optional<Tokens> tokenize(discord4j.core.object.entity.Message message) {
        return message.getContent().filter(tokenizer::isValid).map(tokenizer::tokenize);
    }

    private static String[] getFullMessageAsArray(Tokens tokens) {
        return Stream.concat(Stream.of(tokens.getCommand()), Arrays.stream(tokens.getParams())).toArray(String[]::new);
    }
}
