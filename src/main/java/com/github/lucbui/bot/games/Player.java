package com.github.lucbui.bot.games;

import reactor.core.publisher.Mono;

public interface Player<T extends Game> extends HasId {
    Mono<Void> message(String msg);
}
