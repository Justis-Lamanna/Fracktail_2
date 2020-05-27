package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.context.DiscordCommandUseContext;
import com.github.lucbui.magic.command.func.invoke.CommandFallback;
import com.github.lucbui.magic.exception.CommandValidationException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * A Default Command Handler, used if no other is specified.
 * This basic handler handles:
 * Tokenizing messages
 * Validating commands
 * Executing commands
 */
public class DefaultDiscordCommandHandler implements CommandHandler<MessageCreateEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDiscordCommandHandler.class);

    private final Tokenizer tokenizer;
    private final CommandBank commandBank;
    private final CommandFallback commandFallback;

    /**
     * Initialize DefaultCommandHandler
     * @param tokenizer The tokenizer to use
     * @param commandBank A list of commands
     */
    public DefaultDiscordCommandHandler(Tokenizer tokenizer, CommandBank commandBank, CommandFallback commandFallback) {
        this.tokenizer = tokenizer;
        this.commandBank = commandBank;
        this.commandFallback = commandFallback;
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(!event.getMember().isPresent() && !event.getMessage().getAuthor().map(User::isBot).orElse(false)){
            LOGGER.info("Received message from {} via DM: {}",
                    event.getMessage().getAuthor().map(User::getUsername).orElse("???"),
                    event.getMessage().getContent().orElse("???"));
        }

        CommandUseContext ctx = DiscordCommandUseContext.from(event);

        return tokenizer.tokenizeToMono(ctx)
                .zipWhen(tokens -> commandBank.getCommand(tokens, ctx).map(BCommand::getBehavior).defaultIfEmpty(commandFallback.getNoCommandFound()))
                .flatMap(tokensCmd -> event.getMessage().getChannel().flatMapMany(mc -> mc.typeUntil(
                        tokensCmd.getT2().execute(tokensCmd.getT1(), ctx)
                                .switchIfEmpty(commandFallback.getCommandUsedIncorrectly().execute(tokensCmd.getT1(), ctx))
                )).then())
                .onErrorResume(CommandValidationException.class, ex -> DiscordUtils.respond(event.getMessage(), ex.getMessage()))
                .onErrorResume(ex -> {
                    LOGGER.error("Error handling message", ex);
                    return DiscordUtils.respond(event.getMessage(), "I'm sorry, I encountered an exception. Please check the logs.");
                });
    }
}
