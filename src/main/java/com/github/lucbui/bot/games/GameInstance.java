package com.github.lucbui.bot.games;

public interface GameInstance {
    default void nextTurn() {
        nextTurn(1);
    }
    void nextTurn(int playersToJump);

    String display();
}
