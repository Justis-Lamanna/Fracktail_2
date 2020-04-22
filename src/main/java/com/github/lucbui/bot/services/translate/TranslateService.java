package com.github.lucbui.bot.services.translate;

import com.github.lucbui.magic.exception.CommandValidationException;
import reactor.core.publisher.Mono;

import java.util.Locale;

public interface TranslateService {
    String getString(String key, Locale locale);

    String getFormattedString(String key, Locale locale, Object... args);

    default String getString(String key){
        return getString(key, null);
    }

    default String getFormattedString(String key, Object... args){
        return getFormattedString(key, null, args);
    }

    default Mono<String> getStringMono(String key, Locale locale) {
        return Mono.fromSupplier(() -> getString(key, locale));
    }

    default Mono<String> getStringMono(String key) {
        return Mono.fromSupplier(() -> getString(key));
    }

    default Mono<String> getFormattedStringMono(String key, Locale locale, Object... args) {
        return Mono.fromSupplier(() -> getFormattedString(key, locale, args));
    }

    default Mono<String> getFormattedStringMono(String key, Object... args) {
        return Mono.fromSupplier(() -> getFormattedString(key, args));
    }

    default Mono<String> getStringErrorMono(String key, Locale locale) {
        return Mono.error(() -> getStringException(key, locale));
    }

    default Mono<String> getStringErrorMono(String key) {
        return Mono.error(() -> getStringException(key));
    }

    default Mono<String> getFormattedStringErrorMono(String key, Locale locale, Object... args) {
        return Mono.error(() -> getFormattedStringException(key, locale, args));
    }

    default Mono<String> getFormattedStringErrorMono(String key, Object... args) {
        return Mono.error(() -> getFormattedStringException(key, args));
    }

    default void getStringThrow(String key, Locale locale) {
        throw getStringException(key, locale);
    }

    default void getStringThrow(String key) {
        throw getStringException(key);
    }

    default void getFormattedStringThrow(String key, Locale locale, Object... args) {
        throw getFormattedStringException(key, locale, args);
    }

    default void getFormattedStringThrow(String key, Object... args) {
        throw getFormattedStringException(key, args);
    }

    default CommandValidationException getStringException(String key, Locale locale) {
        return new CommandValidationException(getString(key, locale));
    }

    default CommandValidationException getStringException(String key) {
        return new CommandValidationException(getString(key));
    }

    default CommandValidationException getFormattedStringException(String key, Locale locale, Object... args) {
        return new CommandValidationException(getFormattedString(key, locale, args));
    }

    default CommandValidationException getFormattedStringException(String key, Object... args) {
        return new CommandValidationException(getFormattedString(key, args));
    }
}
