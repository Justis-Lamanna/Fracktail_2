package com.github.lucbui.bot.model.dictionary.mw;

import java.util.List;

public class EntryMetadata {
    private String id;
    private String uuid;
    private String sort;
    private String src;
    private String section;
    private List<String> stems;
    private boolean offensive;

    public EntryMetadata() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<String> getStems() {
        return stems;
    }

    public void setStems(List<String> stems) {
        this.stems = stems;
    }

    public boolean isOffensive() {
        return offensive;
    }

    public void setOffensive(boolean offensive) {
        this.offensive = offensive;
    }
}
