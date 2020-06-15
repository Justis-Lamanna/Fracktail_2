package com.github.lucbui.bot.model.dictionary.mw;

import java.util.List;

public class VerbalIllustrationList implements DefiningTextEntry {
    private List<VerbalIllustration> verbalIllustrations;

    public VerbalIllustrationList(List<VerbalIllustration> verbalIllustrations) {
        this.verbalIllustrations = verbalIllustrations;
    }

    public List<VerbalIllustration> getVerbalIllustrations() {
        return verbalIllustrations;
    }

    public void setVerbalIllustrations(List<VerbalIllustration> verbalIllustrations) {
        this.verbalIllustrations = verbalIllustrations;
    }
}
