package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.annotation.Sender;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.command.CommandValidator;
import com.github.lucbui.magic.validation.message.MessageValidator;
import com.github.lucbui.magic.validation.user.UserValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultCommandHandler implements CommandHandler {

    private final Tokenizer tokenizer;
    private final MessageValidator messageValidator;
    private final CommandValidator commandValidator;
    private final UserValidator userValidator;
    private final CommandList commandList;

    public DefaultCommandHandler(Tokenizer tokenizer, MessageValidator messageValidator, CommandValidator commandValidator,
                                 UserValidator userValidator, CommandList commandList) {
        this.tokenizer = tokenizer;
        this.messageValidator = messageValidator;
        this.commandValidator = commandValidator;
        this.userValidator = userValidator;
        this.commandList = commandList;
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(messageValidator.validate(event)){
            return event.getMessage()
                    .getContent()
                    .filter(tokenizer::isValid)
                    .map(cmdMessage -> commandList.getCommand(tokenizer.tokenize(cmdMessage).getCommand()))
                    .map(cmd -> {
                        try {
                            if(validateCommand(event, cmd)) {
                                return cmd.getBehavior().execute(event);
                            } else {
                                return Mono.<Void>empty();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return DiscordUtils.respond(event.getMessage(), "I'm sorry, I encountered an exception. Please check the logs.");
                        }
                    }).orElse(Mono.empty());
        }
        return Mono.empty();
    }

    private boolean validateCommand(MessageCreateEvent event, BotCommand cmd) {
        return commandValidator.validate(event, cmd) && event.getMember().map(usr -> userValidator.validate(usr, cmd)).orElse(false);
    }

    @Command(help = "Get help for any command. Usage is !help [command name without exclamation point].")
    public String help(@Param(0) String cmd, @Sender Member user) {
        if(cmd == null) {
            cmd = "help";
        }
        BotCommand command = commandList.getCommand(cmd);
        if(command == null || !userValidator.validate(user, command)) {
            return cmd + " is not a valid command.";
        } else {
            return command.getHelpText();
        }
    }

    @Command(help = "Get a list of all usable commands.")
    public String commands(@Sender Member user) {
        return "Commands are: " + commandList.getAllCommands()
                .stream()
                .filter(cmd -> userValidator.validate(user, cmd))
                .flatMap(cmd -> Arrays.stream(cmd.getNames()))
                .sorted()
                .map(cmd -> "!" + cmd)
                .collect(Collectors.joining(", ")) + ".";
    }
}
