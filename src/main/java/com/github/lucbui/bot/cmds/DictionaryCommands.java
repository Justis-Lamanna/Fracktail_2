package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import com.github.lucbui.bot.services.dictionary.DictionaryService;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Commands
public class DictionaryCommands {
    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private TranslateService translateService;

    @Command
    @CommandParams(value = 1, comparison = ParamsComparison.OR_MORE)
    public Mono<String> lookup(@Params String word) {
        return dictionaryService.lookup(word)
                .map(either -> {
                    if(either.isLeft()) {
                        DictionaryEntry defs = either.left();
                        if (defs.getDefinitions().isEmpty()) {
                            return translateService.getFormattedString("lookup.noDefinition", defs.getWord());
                        } else {
                            String prefix = translateService.getFormattedString("lookup.header", defs.getWord());
                            String urlFriendlyWord = word.replaceAll("\\s", "%20");
                            String suffix = defs.getDefinitions().size() > 8 ?
                                    translateService.getFormattedString("lookup.footer.exceedsMax", urlFriendlyWord) :
                                    translateService.getFormattedString("lookup.footer", urlFriendlyWord);
                            return defs.getDefinitions().stream()
                                    .limit(8)
                                    .map(def -> translateService.getFormattedString("lookup.definition", def.getDefinition(), def.getPartOfSpeech()))
                                    .collect(Collectors.joining("\n", prefix + "\n", "\n" + suffix));
                        }
                    } else {
                        return translateService.getFormattedString("lookup.suggest", word, either.right().getCorrections().stream().limit(10).collect(Collectors.joining(", ")));
                    }
                });
    }
}
