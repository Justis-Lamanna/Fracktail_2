package com.github.lucbui.bot.services.dictionary;

import com.github.lucbui.bot.model.dictionary.Autocorrect;
import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import com.github.lucbui.magic.util.Either;
import reactor.core.publisher.Mono;

public interface DictionaryService {
    Mono<Either<DictionaryEntry, Autocorrect>> lookup(String word);
}
