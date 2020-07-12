package com.github.lucbui.bot.games.action;

import com.github.lucbui.bot.games.*;

public class PassAction<T extends Game, G extends AbstractGameInstance<T, ? extends Player<T>, G>> implements LegalAction<T, G> {
    public static final String INVALID_PLAYER_PASSED = "INVALID_PLAYER_PASSED";

    @Override
    public MoveLegality isLegal(Id player, G instance) {
        return MoveLegality.legalIf(instance.isTurn(player), INVALID_PLAYER_PASSED);
    }

    @Override
    public MoveLegality performIfLegal(Id player, G instance) {
        instance.nextTurn();
        return MoveLegality.legal();
    }
}
