package com.github.lucbui.bot.model.dictionary;

import java.util.List;

public class DictionaryEntry {
    private String word;
    private List<String> definitions;

    public DictionaryEntry(String word, List<String> definitions) {
        this.word = word;
        this.definitions = definitions;
    }

    public String getWord() {
        return word;
    }

    public List<String> getDefinitions() {
        return definitions;
    }
}
