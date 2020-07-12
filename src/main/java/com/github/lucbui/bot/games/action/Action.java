package com.github.lucbui.bot.games.action;

import com.github.lucbui.bot.games.*;

public interface Action<T extends Game, G extends AbstractGameInstance<T, ? extends Player<T>, G>> {
    MoveLegality perform(Id player, G instance);
}
