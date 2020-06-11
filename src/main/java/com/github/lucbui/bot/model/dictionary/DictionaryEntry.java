package com.github.lucbui.bot.model.dictionary;

import java.util.List;

public class DictionaryEntry {
    private final String word;
    private final String syllableWord;
    private final List<Definition> definitions;

    public DictionaryEntry(String word, String syllableWord, List<Definition> definitions) {
        this.word = word;
        this.syllableWord = syllableWord;
        this.definitions = definitions;
    }

    public String getWord() {
        return word;
    }

    public String getSyllableWord() {
        return syllableWord;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public static class Definition {
        private final String partOfSpeech;
        private final String definition;

        public Definition(String partOfSpeech, String definition) {
            this.partOfSpeech = partOfSpeech;
            this.definition = definition;
        }

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public String getDefinition() {
            return definition;
        }
    }
}
