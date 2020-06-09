package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.dictionary.DictionaryService;
import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.CommandParams;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.annotation.Param;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@Commands
public class DictionaryCommands {
    @Autowired
    private DictionaryService dictionaryService;

    @Command
    @CommandParams(1)
    public Mono<String> lookup(@Param(0) String word) {
        return dictionaryService.lookup(word)
                .map(defs -> {
                    if(defs.getDefinitions().isEmpty()) {
                        return "No definition found.";
                    } else {
                        return StringUtils.capitalize(word) + ":\n" + String.join("\n", defs.getDefinitions());
                    }
                });
    }
}
