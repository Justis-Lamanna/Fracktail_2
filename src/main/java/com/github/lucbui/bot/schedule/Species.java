package com.github.lucbui.bot.schedule;

import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.object.util.Snowflake;

import java.time.MonthDay;

public enum Species {
    DRAGONS("Dragon", Snowflake.of(560500269419331585L)),
    RED_PANDAS("Red Panda", Snowflake.of(560499243375394827L)),
    FOXES("Fox", Snowflake.of(560499430445285388L)),
    CATS("Cat", Snowflake.of(560500264365326336L)),
    BIG_CATS("Big Cat", Snowflake.of(560500710199001098L)),
    LYNXES("Lynx", Snowflake.of(602719185641930772L)),
    DOGS("Dog", Snowflake.of(560501192778842112L)),
    WOLVES("Wolf", Snowflake.of(560501222583566336L)),
    POKEMON("Pokémon", Snowflake.of(560501744933797898L)),
    RABBIT("Rabbit", Snowflake.of(611976597255356522L)),
    OTTER("Otter", Snowflake.of(611990012720054342L)),
    SWOLVS("Swolvy", Snowflake.of(248612704019808258L), true),
    BEARS("Bear", Snowflake.of(131054283557699585L), true),
    POSSUMS("Possum", Snowflake.of(72873307635982336L), true),
    BATS("Bat", Snowflake.of(203918193847304194L), true),
    SHEEP("Sheep", Snowflake.of(159313017094275072L), true),
    DUCKS("Duck", Snowflake.of(126548598618980352L), true),
    OWLS("Owl", Snowflake.of(224052659252887554L), true)
    ;

    private final String speciesName;
    private final Snowflake id;
    private final boolean user;

    Species(String speciesName, Snowflake id) {
        this.speciesName = speciesName;
        this.id = id;
        this.user = false;
    }

    Species(String speciesName, Snowflake id, boolean user) {
        this.speciesName = speciesName;
        this.id = id;
        this.user = user;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public String getMention() {
        if(user) {
            return DiscordUtils.getMentionFromId(this.id);
        } else {
            return DiscordUtils.getRoleMentionFromId(this.id);
        }
    }
}
