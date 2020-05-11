package com.github.lucbui.bot.config;

import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.store.CommandListFallback;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UsageCommandListFallback implements CommandListFallback {
    @Autowired
    private TranslateService translateService;

    @Override
    public Mono<BotCommand> noCommandFound(Tokens tokens, CommandUseContext ctx) {
        return Mono.empty();
    }

    @Override
    public Mono<BotCommand> commandUsedWrong(Tokens tokens, CommandUseContext ctx, List<BotCommand> otherCandidates) {
        if(CollectionUtils.isEmpty(otherCandidates)){
            return Mono.empty();
        } else {
            String officialName = otherCandidates.get(0).getName();
            return Mono.just(new UsageBotCommand(officialName));
        }
    }

    private class UsageBotCommand extends BotCommand {
        public UsageBotCommand(String command) {
            super(command,
                    event -> DiscordUtils.respond(event.getMessage(), translateService.getString(TranslateHelper.usageKey(command))));
        }
    }
}
