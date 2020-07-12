package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.games.*;
import com.github.lucbui.bot.games.action.Action;
import com.github.lucbui.bot.games.action.ForfeitAction;
import com.github.lucbui.bot.games.action.PassAction;
import com.github.lucbui.bot.games.checkers.Checkers;
import com.github.lucbui.bot.games.checkers.CheckersColor;
import com.github.lucbui.bot.games.checkers.CheckersGameInstance;
import com.github.lucbui.bot.games.checkers.CheckersPlayer;
import com.github.lucbui.bot.games.checkers.action.MoveAction;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.exception.CommandValidationException;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Commands
public class CheckersCommands {
    private final Map<String, AtomicReference<CheckersGameInstance>> matches = new HashMap<>();

    private static final Action<Checkers, CheckersGameInstance> PASS_ACTION = new PassAction<>();
    private static final Action<Checkers, CheckersGameInstance> FORFEIT_ACTION = new ForfeitAction<>();

    @Command
    @CommandParams(2)
    public Mono<Boolean> startGame(CommandUseContext ctx, @Param(0) String game, @Param(1) String opponent) {
        if(!StringUtils.equalsAnyIgnoreCase(game, "checkers", "draughts")) {
            return ctx.respond("Only games allowed are: checkers, draughts").thenReturn(true);
        }
        if (ctx instanceof DiscordCommandUseContext) {
            DiscordCommandUseContext dCtx = (DiscordCommandUseContext) ctx;
            DiscordClient bot = dCtx.getEvent().getClient();
            Snowflake opponentSnowflake =
                    DiscordUtils.toSnowflakeFromMentionOrLiteral(opponent)
                            .orElseThrow(() -> new CommandValidationException("Invalid snowflake or @ format"));

            if (matches.containsKey(ctx.getUserId()) && matches.get(ctx.getUserId()).get() != null) {
                return ctx.respond("A game is already in progress. !forfeit to end your current match.").thenReturn(true);
            } else if (matches.containsKey(opponentSnowflake.asString()) && matches.get(opponentSnowflake.asString()).get() != null) {
                return ctx.respond("That user is already playing a game. Please try again later.").thenReturn(true);
            }
//            } else if(opponentSnowflake.asString().equalsIgnoreCase(ctx.getUserId())) {
//                return ctx.respond("Sorry, you cannot play against yourself.").thenReturn(true);
//            }

            AtomicReference<CheckersGameInstance> gameState = new AtomicReference<>(null);

            return Flux.just(opponentSnowflake, Snowflake.of(ctx.getUserId()))
                    .flatMap(bot::getUserById)
                    .collectList()
                    .flatMap(users -> {
                                CheckersGameInstance instance = new CheckersGameInstance(
                                        CheckersPlayer.discord(users.get(0), CheckersColor.RED),
                                        CheckersPlayer.discord(users.get(1), CheckersColor.BLACK));
                                if (gameState.compareAndSet(null, instance)) {
                                    synchronized (matches) {
                                        matches.put(users.get(0).getId().asString(), gameState);
                                        matches.put(users.get(1).getId().asString(), gameState);
                                    }
                                    return instance.messagePlayers(
                                            users.get(1).getUsername() + " challenged you to a game of Checkers! You are red, and you go first.\n" + instance.display(),
                                            "You challenged " + users.get(0).getUsername() + " to a game of Checkers! They go first.").thenReturn(true);
                                } else {
                                    return ctx.respond("A game is already in progress. !forfeit to end your current match.").thenReturn(true);
                                }
                            }
                    )
                    .onErrorResume(ex -> ctx.respond("Unable to find one or more of the specified users.").thenReturn(true))
                    .thenReturn(true);
        }
        return Mono.just(true);
    }

    @Command
    public Mono<Boolean> forfeit(CommandUseContext ctx) {
        AtomicReference<CheckersGameInstance> gameState = matches.get(ctx.getUserId());
        return doAction(ctx, gameState, FORFEIT_ACTION);
    }

    @Command
    public Mono<Boolean> pass(CommandUseContext ctx) {
        AtomicReference<CheckersGameInstance> gameState = matches.get(ctx.getUserId());
        return doAction(ctx, gameState, PASS_ACTION);
    }

    @Command
    @CommandParams(value = 1, comparison = ParamsComparison.OR_MORE)
    public Mono<Boolean> move(CommandUseContext ctx, @Params String movementParam) {
        AtomicReference<CheckersGameInstance> gameState = matches.get(ctx.getUserId());
        return parseMoveAction(movementParam)
                .map(ma -> doAction(ctx, gameState, ma))
                .orElse(ctx.respond("Uh...").thenReturn(true));
    }

    private Position getPosFrom(String col, String row) {
        int colNum = (col.toUpperCase().charAt(0) - 'A');
        int rowNum = Integer.parseInt(row) - 1;
        return new Position(rowNum, colNum);
    }

    private Optional<MoveAction> parseMoveAction(String movement) {
        List<Position> positions = Arrays.stream(movement.split("->"))
                .map(String::trim)
                .map(s -> getPosFrom(s.substring(0, 1), s.substring(1, 2)))
                .collect(Collectors.toList());
        if(positions.size() == 1) {
            return Optional.empty();
        } else {
            return Optional.of(new MoveAction(positions.get(0), positions.subList(1, positions.size())));
        }
    }

    private <G extends Game, GI extends AbstractGameInstance<G, ? extends Player<G>, GI> & Copyable<GI>>
    Mono<Boolean> doAction(CommandUseContext ctx, AtomicReference<GI> game, Action<G, GI> action) {
        if(game == null) {
            return ctx.respond("No game is running.").thenReturn(true);
        }
        Id player = new Id(ctx.getUserId());
        GI newInstance, oldInstance;
        MoveLegality legality;
        do {
            oldInstance = game.get();
            if(oldInstance != null) {
                newInstance = oldInstance.copy();
                legality = newInstance.performAction(player, action);
            } else {
                newInstance = null;
                legality = null;
            }
        } while(!game.compareAndSet(oldInstance, newInstance));
        if(newInstance == null) {
            return ctx.respond("No game is running.").thenReturn(true);
        } else {
            newInstance.updatePlayerState();
            if(newInstance.getNumberOfPlayers() == 1) {
                //Remaining player is automatically a winner
                return newInstance.getPlayerInPlay().message("You win!").thenReturn(true);
            } else if(newInstance.getNumberOfPlayers() != 0) {
                if(legality.isValidMove()) {
                    //Display board, prompt for move
                    return oldInstance.getPlayerInPlay().message("Good move!")
                            .then(newInstance.getPlayerInPlay().message("It's your turn!\n" + newInstance.display()))
                            .thenReturn(true);
                } else {
                    String respond = legality.getMessages().map(msg -> "Invalid move: " + msg + ".").orElse("Invalid move.");
                    return ctx.respond(respond).thenReturn(true);
                }
            } else {
                return Mono.just(true);
            }
        }
    }
}
