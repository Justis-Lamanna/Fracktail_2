package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.dictionary.Autocorrect;
import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import com.github.lucbui.bot.services.dictionary.DictionaryService;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.stream.Collectors;

@Commands
public class DictionaryCommands {
    private static final int MAX_DEFINITIONS_TO_SHOW = 8;
    private static final int MAX_AUTOCORRECTS_TO_SHOW = 5;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private TranslateService translateService;

    @Command
    @CommandParams(value = 1, comparison = ParamsComparison.OR_MORE)
    public Mono<String> lookup(@Params String word) throws UnsupportedEncodingException {
        String normalizedWord = StringUtils.capitalize(word);
        String urlFriendlyWord = URLEncoder.encode(word, "UTF-8");
        return dictionaryService.lookup(word)
                .map(either -> {
                    if(either.isLeft()) {
                        DictionaryEntry defs = either.left();
                        if (defs.getDefinitions().isEmpty()) {
                            return translateService.getFormattedString("lookup.noDefinition", defs.getWord());
                        } else {
                            String prefix = defs.getSyllableWord() == null ?
                                    translateService.getFormattedString("lookup.header.noSyllable", defs.getWord()) :
                                    translateService.getFormattedString("lookup.header", defs.getWord(), defs.getSyllableWord());
                            String suffix = defs.getDefinitions().size() > MAX_DEFINITIONS_TO_SHOW ?
                                    translateService.getFormattedString("lookup.footer.exceedsMax", urlFriendlyWord) :
                                    translateService.getFormattedString("lookup.footer", urlFriendlyWord);
                            return defs.getDefinitions().stream()
                                    .limit(MAX_DEFINITIONS_TO_SHOW)
                                    .map(def -> translateService.getFormattedString("lookup.definition", def.getDefinition(), def.getPartOfSpeech()))
                                    .collect(Collectors.joining("\n", prefix + "\n", "\n" + suffix));
                        }
                    } else {
                        Autocorrect ac = either.right();
                        if(ac.getCorrections().isEmpty()) {
                            return translateService.getFormattedString("lookup.noDefinition", normalizedWord);
                        } else {
                            return translateService.getFormattedString("lookup.noDefinition.suggest", normalizedWord, ac.getCorrections()
                                    .stream().limit(MAX_AUTOCORRECTS_TO_SHOW).collect(Collectors.joining(", ")));
                        }
                    }
                });
    }
}
