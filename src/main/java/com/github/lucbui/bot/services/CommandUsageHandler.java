package com.github.lucbui.bot.services;

import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.NoCommandFoundHandler;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.token.Tokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CommandUsageHandler implements NoCommandFoundHandler {
    @Autowired
    private TranslateService translateService;

    @Autowired
    private CommandList commandList;

    @Override
    public Mono<BotCommand> getDefaultBotCommand(Tokens tokens) {
        String name = commandList.getNormalizedName(tokens.getCommand());
        if(name == null){
            return Mono.empty();
        } else {
            return Mono.error(() -> translateService.getStringException(name + ".usage"));
        }
    }
}
