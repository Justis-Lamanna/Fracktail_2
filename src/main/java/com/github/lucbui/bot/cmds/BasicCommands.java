package com.github.lucbui.bot.cmds;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.annotation.Commands;
import com.github.lucbui.calendarfun.annotation.Permissions;
import com.github.lucbui.calendarfun.annotation.Timeout;
import com.github.lucbui.bot.calendar.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@Commands
public class BasicCommands {
    private static final int OLDEST_POSSIBLE_YEAR = 1903;

    @Autowired
    private CalendarService calendarService;

    @Command(help = "Perform arithmetic. Usage is !math [expression]")
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    public String math() {
        return "The answer is 3";
    }

    @Command(help = "Taunt the others in your server with a command they can't use")
    @Permissions("admin")
    public String admin() {
        return "This is a cool command that only admins can use!";
    }

    @Command(help = "RAFO!")
    @Timeout(value = 5, unit = ChronoUnit.MINUTES)
    public String rafo() {
        return "<:rafo1:596138147285434415><:rafo2:596138147797270538><:rafo3:596138379603869697><:rafo4:596138380132089879>\n" +
                "<:rafo5:596138380211781641><:rafo6:596138491469889536><:rafo7:596138588584804373><:rafo8:596138610193858581>\n" +
                "<:rafo9:596138646130917376><:rafo10:596138678108291082><:rafo11:596138697607348257><:rafo12:596138718817943552>\n" +
                "<:rafo13:596138741052211210><:rafo14:596138758160515073><:rafo15:596138771779682315><:rafo16:596138788984586268>";
    }
}
