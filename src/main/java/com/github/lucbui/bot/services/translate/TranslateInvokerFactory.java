package com.github.lucbui.bot.services.translate;

import com.github.lucbui.bot.annotation.Translate;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.command.func.invoke.DefaultInvokerFactory;
import com.github.lucbui.magic.command.func.invoke.Invoker;
import com.github.lucbui.magic.exception.BotException;
import discord4j.core.object.entity.Guild;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Locale;

public class TranslateInvokerFactory extends DefaultInvokerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranslateInvokerFactory.class);

    private final TranslateService translateService;

    public TranslateInvokerFactory(TranslateService translateService) {
        this.translateService = translateService;
    }

    @Override
    public Invoker getInvokerFor(Object beanToInvoke, Method method) {
        if(method.isAnnotationPresent(Translate.class)) {
            Translate translate = method.getAnnotation(Translate.class);
            if(method.getReturnType().equals(Void.TYPE)) {
                if(StringUtils.isNotBlank(translate.value())) {
                    LOGGER.debug("+- Marking void method {} as returning translate key {}", method.getName(), translate.value());
                    return (ctx, params) -> getStringHandlingMono(ctx, translate.value());
                } else {
                    throw new BotException("@Translate void must have key");
                }
            }
            if(method.getReturnType().equals(String.class)) {
                LOGGER.debug("+- Marking String method {} as returning translate key", method.getName());
                return (ctx, params) -> {
                    String r = (String)method.invoke(beanToInvoke, params);
                    return getStringHandlingMono(ctx, r);
                };
            } else if(method.getReturnType().equals(TranslateKeyArgs.class)) {
                LOGGER.debug("+- Marking TranslateKeyArgs method {} as returning translate key + args", method.getName());
                return (ctx, params) -> {
                    TranslateKeyArgs tka = (TranslateKeyArgs)method.invoke(beanToInvoke, params);
                    return getTranslateKeyArgsHandlingMono(ctx, tka);
                };
            } else if(method.getReturnType().equals(Mono.class)) {
                LOGGER.debug("+- Marking Mono method {} as returning translation", method.getName());
                return (ctx, params) -> {
                    Mono<?> mono = (Mono<?>)method.invoke(beanToInvoke, params);
                    return mono.flatMap(obj -> {
                                if(obj instanceof String) {
                                    return getStringHandlingMono(ctx, (String) obj);
                                } else if(obj instanceof TranslateKeyArgs) {
                                    return getTranslateKeyArgsHandlingMono(ctx, (TranslateKeyArgs)obj);
                                } else if(obj instanceof Object[]) {
                                    if(StringUtils.isBlank(translate.value())) {
                                        return Mono.error(new BotException("@Translate with Mono<Object[]> must have a key"));
                                    }
                                    return getTranslateKeyArgsHandlingMono(ctx, new TranslateKeyArgs(translate.value(), (Object[]) obj));
                                } else {
                                    return Mono.error(new BotException("@Translate Mono must return String or TranslateKeyArgs"));
                                }
                            });
                };
            } else if(method.getReturnType().equals(Object[].class)) {
                if(StringUtils.isEmpty(translate.value())) {
                    throw new BotException("@Translate returning Object[] must have key");
                }
                LOGGER.debug("+- Marking Object[] method {} as returning translate key {} + args", method.getName(), translate.value());
                return (ctx, params) -> {
                    Object[] returns = (Object[]) method.invoke(beanToInvoke, params);
                    return getTranslateKeyArgsHandlingMono(ctx, new TranslateKeyArgs(translate.value(), returns));
                };
            }
        }
        return super.getInvokerFor(beanToInvoke, method);
    }

    private Mono<Boolean> getTranslateKeyArgsHandlingMono(CommandUseContext ctx, TranslateKeyArgs tka) {
        return getLocale(ctx)
                .flatMap(locale -> translateService.getFormattedStringMono(tka.getKey(), locale, tka.getArgs()))
                .switchIfEmpty(translateService.getFormattedStringMono(tka.getKey(), tka.getArgs()))
                .flatMap(ctx::respond)
                .thenReturn(true);
    }

    private Mono<Boolean> getStringHandlingMono(CommandUseContext ctx, String r) {
        return getLocale(ctx)
                .flatMap(locale -> translateService.getStringMono(r, locale))
                .switchIfEmpty(translateService.getStringMono(r))
                .flatMap(ctx::respond)
                .thenReturn(true);
    }

    private Mono<Locale> getLocale(CommandUseContext context) {
        if(context instanceof DiscordCommandUseContext) {
            DiscordCommandUseContext dCtx = (DiscordCommandUseContext) context;
            return dCtx.getEvent().getGuild()
                    .map(Guild::getPreferredLocale)
                    .map(dLocale -> new Locale(
                            StringUtils.defaultString(dLocale.getLanguage()),
                            StringUtils.defaultString(dLocale.getCountry()),
                            StringUtils.defaultString(dLocale.getVariant())));
        }
        return Mono.empty();
    }
}
