package com.github.lucbui.bot.games.action;

import com.github.lucbui.bot.games.*;

public interface LegalAction<T extends Game, G extends AbstractGameInstance<T, ? extends Player<T>, G>> extends Action<T, G> {
    MoveLegality isLegal(Id playerId, G instance);

    @Override
    default MoveLegality perform(Id player, G instance) {
        MoveLegality legality = isLegal(player, instance);
        if(legality.isValidMove()) {
            return performIfLegal(player, instance);
        }
        return legality;
    }

    MoveLegality performIfLegal(Id playerId, G instance);
}
