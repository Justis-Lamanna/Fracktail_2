package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.annotation.Translate;
import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

@Commands
public class BasicCommands {
    @Autowired
    private TranslateService translateService;

    @Autowired
    private DiscordClient bot;

    @Command
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    @Translate("math.text")
    public void math() {}

    @Command
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    @Translate("weather.text")
    public void weather() {
    }

    @Command
    @Translate("invite.text")
    public void invite() {
    }

    @Command
    @Permissions("owner")
    @Translate("admin.text")
    public void admin(){
    }

    @Command
    @Timeout(value = 5, unit = ChronoUnit.MINUTES)
    public String rafo() {
        return "<:rafo1:596138147285434415><:rafo2:596138147797270538><:rafo3:596138379603869697><:rafo4:596138380132089879>\n" +
                "<:rafo5:596138380211781641><:rafo6:596138491469889536><:rafo7:596138588584804373><:rafo8:596138610193858581>\n" +
                "<:rafo9:596138646130917376><:rafo10:596138678108291082><:rafo11:596138697607348257><:rafo12:596138718817943552>\n" +
                "<:rafo13:596138741052211210><:rafo14:596138758160515073><:rafo15:596138771779682315><:rafo16:596138788984586268>";
    }

    @Command
    @CommandParams(1)
    public Mono<String> whodat(@Param(0) String userId) {
        if(!DiscordUtils.isValidSnowflake(userId)) {
            return translateService.getStringMono(TranslateHelper.usageKey("whodat"));
        }
        if(bot.getSelfId().map(selfId -> selfId.asString().equals(userId)).orElse(false)) {
            return translateService.getStringMono("whodat.me");
        }
        return bot.getUserById(Snowflake.of(userId))
                .map(user -> translateService.getFormattedString("whodat.success", user.getUsername()))
                .onErrorReturn(translateService.getString("whodat.failure"));
    }

    @Command
    @Timeout(value = 30)
    @CommandParams(value = 1, comparison = ParamsComparison.OR_LESS)
    public Mono<String> timestampify(CommandUseContext ctx, @Param(0) String snowflake) {
        return Mono.justOrEmpty(snowflake)
                .defaultIfEmpty(ctx.getUserId())
                .map(Snowflake::of)
                .map(f -> translateService.getFormattedString("timestampify.success", Date.from(f.getTimestamp())))
                .onErrorResume(ex -> Mono.just(translateService.getString("timestampify.validation.invalidSnowflake")));
    }

    @Command
    @Translate("updog.text")
    public void updog() {
    }

    private static final Set<String> YES_DOG_FOOD = SetUtils.hashSet("dog food", "homework");
    private static final Set<String> YES_CAT_FOOD = SetUtils.hashSet("fish", "fishy");
    private static final Set<String> WHAT_FOOD = SetUtils.hashSet("lucbui", "fracktail", "me");

    @Command
    @CommandParams(1)
    @Translate
    public String canDogsEat(@Param(0) String food) {
        if(YES_DOG_FOOD.contains(food.toLowerCase())) {
            return "candogseat.yes";
        }
        if(WHAT_FOOD.contains(food.toLowerCase()) || DiscordUtils.isMention(food)) {
            return "candogseat.what";
        }
        return "candogseat.maybe";
    }

    @Command
    @CommandParams(1)
    @Translate
    public String canCatsEat(@Param(0) String food) {
        if(YES_CAT_FOOD.contains(food.toLowerCase())) {
            return "cancatseat.no";
        }
        if(WHAT_FOOD.contains(food.toLowerCase()) || DiscordUtils.isMention(food)) {
            return "cancatseat.what";
        }
        return "cancatseat.maybe";
    }

    @Command(aliases = "candergseat")
    @CommandParams(1)
    @Translate
    public String canDragonsEat(@Param(0) String food) {
        if(WHAT_FOOD.contains(food.toLowerCase()) || DiscordUtils.isMention(food)) {
            return "candragonseat.reallyyes";
        }
        return "candragonseat.yes";
    }
}
