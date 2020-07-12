package com.github.lucbui.bot.games.checkers;

import com.github.lucbui.bot.games.Id;
import com.github.lucbui.bot.games.Player;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class CheckersPlayer implements Player<Checkers> {
    private final Id id;
    private final CheckersColor color;
    private final Function<String, Mono<Void>> msgFunc;

    public CheckersPlayer(Id id, CheckersColor color, Function<String, Mono<Void>> msgFunc) {
        this.id = id;
        this.color = color;
        this.msgFunc = msgFunc;
    }

    public CheckersColor getColor() {
        return color;
    }

    @Override
    public Mono<Void> message(String msg) {
        return msgFunc.apply(msg);
    }

    @Override
    public Id id() {
        return id;
    }

    public static CheckersPlayer discord(User user, CheckersColor color) {
        return new CheckersPlayer(
                new Id(user.getId().asString()),
                color,
                msg -> user.getPrivateChannel().flatMap(pc -> pc.createMessage(msg)).then()
        );
    }
}
