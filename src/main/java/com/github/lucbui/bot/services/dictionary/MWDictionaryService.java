package com.github.lucbui.bot.services.dictionary;

import com.github.lucbui.bot.dao.MerriamWebsterDao;
import com.github.lucbui.bot.model.dictionary.Autocorrect;
import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import com.github.lucbui.bot.model.dictionary.mw.IntermediateDictionaryResponse;
import com.github.lucbui.magic.util.Either;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class MWDictionaryService implements DictionaryService {

    @Autowired
    private MerriamWebsterDao merriamWebsterDao;

    @Override
    public Mono<Either<DictionaryEntry, Autocorrect>> lookup(String word) {
        return merriamWebsterDao.lookup(word)
                .map(either -> either.map(idrs -> {
                    List<DictionaryEntry.Definition> definitions = new ArrayList<>();
                    for (IntermediateDictionaryResponse idr : idrs) {
                        for (String def : idr.getShortDef()) {
                            DictionaryEntry.Definition definition = new DictionaryEntry.Definition(
                                    idr.getFunctionalLabel(),
                                    def);
                            definitions.add(definition);
                        }
                    }
                    return new DictionaryEntry(getDisplayWord(word), getSyllableWord(idrs), definitions);
                }, Autocorrect::new));
    }

    private String getDisplayWord(String word) {
        return StringUtils.capitalize(word);
    }

    private String getSyllableWord(List<IntermediateDictionaryResponse> idrs) {
        if(idrs.isEmpty()) {
            return null;
        }
        return idrs.get(0).getHeadwordInfo().getHeadword().replaceAll("\\*", "·");
    }
}
