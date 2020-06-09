package com.github.lucbui.bot.model.dictionary;

import java.util.List;

public class DictionaryEntry {
    private String word;
    private List<Definition> definitions;

    public DictionaryEntry(String word, List<Definition> definitions) {
        this.word = word;
        this.definitions = definitions;
    }

    public String getWord() {
        return word;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public static class Definition {
        private String partOfSpeech;
        private String definition;

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
