package com.github.lucbui.bot.games.action;

import com.github.lucbui.bot.games.*;

public class ForfeitAction<T extends Game, G extends AbstractGameInstance<T, ? extends Player<T>, G>> implements Action<T, G> {
    public static final String FORFEIT = "FORFEIT";

    @Override
    public MoveLegality perform(Id player, G instance) {
        instance.forfeit(player);
        return MoveLegality.legal();
    }
}
