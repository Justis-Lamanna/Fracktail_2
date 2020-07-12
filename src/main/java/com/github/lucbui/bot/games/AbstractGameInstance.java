package com.github.lucbui.bot.games;

import com.github.lucbui.bot.games.action.Action;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

public abstract class AbstractGameInstance<T extends Game, U extends Player<T>, THIS extends AbstractGameInstance<T, U, THIS>>
    implements GameInstance {
    protected final List<U> players;
    protected int turnCounter;

    protected Deque<TurnAction> actions;

    public AbstractGameInstance(List<U> players, int turnCounter, Deque<TurnAction> actions) {
        this.players = new ArrayList<>(players);
        this.turnCounter = turnCounter;
        this.actions = actions;
    }

    public AbstractGameInstance(List<U> players, int turnCounter) {
        this(players, turnCounter, new LinkedList<>());
    }

    public U getPlayerInPlay() {
        return player(turnCounter);
    }

    public U player(int idx) {
        return players.get(idx);
    }

    public U player(Id id) {
        return playerOrEmpty(id).orElseThrow(() -> new NoSuchElementException("No player with ID " + id.getIdentifier() + " found."));
    }

    public Optional<U> playerOrEmpty(Id id) {
        return players.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    public List<U> getPlayers() {
        return new ArrayList<>(players);
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public boolean isTurn(Id player) {
        return getPlayerInPlay().id().equals(player);
    }

    public void forfeit(Id player) {
        if(!players.removeIf(p -> p.id().equals(player))) {
            throw new NoSuchElementException("No player with ID " + player.getIdentifier() + " found.");
        }
    }

    public void forfeitOrDoNothing(Id player) {
        players.removeIf(p -> p.id().equals(player));
    }

    public Mono<Void> messagePlayers(String toPlayerInPlay, String toPlayersWaiting) {
        Mono<Void> messagePlayer = getPlayerInPlay().message(toPlayerInPlay);
        Mono<Void> messageOthers = Flux.fromStream(IntStream.range(0, getNumberOfPlayers()).boxed())
                                        .filter(i -> i != turnCounter)
                                        .map(this::player)
                                        .flatMap(player -> player.message(toPlayersWaiting))
                                        .then();
        return messagePlayer.then(messageOthers);
    }

    public MoveLegality performAction(Id player, Action<T, THIS> action) {
        MoveLegality legality = action.perform(player, (THIS) this);
        if(legality.isValidMove()) {
            actions.push(new TurnAction(player, action));
        }
        return legality;
    }

    public abstract void updatePlayerState();

    @Override
    public void nextTurn(int playersToJump) {
        if(!players.isEmpty()) {
            turnCounter = (turnCounter + playersToJump) % players.size();
        }
    }

    protected class TurnAction {
        private final Id player;
        private final Action<T, THIS> action;

        public TurnAction(Id player, Action<T, THIS> action) {
            this.player = player;
            this.action = action;
        }
    }
}
