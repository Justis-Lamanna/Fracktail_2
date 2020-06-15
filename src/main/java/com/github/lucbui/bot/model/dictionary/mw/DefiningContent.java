package com.github.lucbui.bot.model.dictionary.mw;

public class DefiningContent implements DefiningTextEntry {
    private String content;

    public DefiningContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
