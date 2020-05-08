package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.Poll;
import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.exception.BotException;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Commands
public class PollCommands {
    @Autowired
    private DiscordClient bot;

    @Autowired
    private TaskScheduler taskScheduler;

    @PostConstruct
    private void attachListeners() {
    }

    @Command
    public Mono<Void> startpoll(MessageCreateEvent event) {
        String message = "Vote now on your phones!";
        List<Poll.Choice> choices = Arrays.asList(
                new Poll.Choice("❤", "I love you"),
                new Poll.Choice("\uD83D\uDC93", "I really love you")
        );
        Instant expiryTime = Instant.now().plus(5, ChronoUnit.MINUTES);
        Poll poll = new Poll(message, expiryTime, choices);

        return event.getMessage().getChannel()
                .flatMap(mc -> mc.createMessage(createMessage(message, choices)))
                .doOnNext(msg -> registerPoll(msg.getChannelId(), msg.getId(), poll))
                .then();
    }

    private void registerPoll(Snowflake channelId, Snowflake id, Poll poll) {
        taskScheduler.schedule(() -> {
            bot.getMessageById(channelId, id)
                    .flatMapIterable(Message::getReactions)
                    .flatMap(reaction -> Mono.justOrEmpty(getChoiceForReact(poll.getChoices(), reaction))
                            .map(choice -> Tuples.of(choice, reaction.getCount())))
                    .sort(Comparator.comparingInt(Tuple2::getT2))
                    .collectList()
                    .flatMap(results -> bot.getChannelById(channelId)
                            .cast(TextChannel.class)
                            .flatMap(tc -> {
                                if(results.isEmpty()) {
                                    return tc.createMessage("Nobody voted...");
                                } else {
                                    return tc.createMessage("We have a winner! The winner is: " + results.get(results.size() - 1).getT1().getMeaning());
                                }
                            })
                            .thenReturn(results))
                    .block();
        }, poll.getExpiryTime());
    }

    private Optional<Poll.Choice> getChoiceForReact(List<Poll.Choice> choices, Reaction reaction) {
        return choices.stream()
                .filter(choice -> choice.getOption().equals(getEmojiString(reaction)))
                .findFirst();
    }

    private String getEmojiString(Reaction reaction) {
        Optional<ReactionEmoji.Unicode> asUnicode = reaction.getEmoji().asUnicodeEmoji();
        Optional<ReactionEmoji.Custom> asCustom = reaction.getEmoji().asCustomEmoji();
        if(asCustom.isPresent()) {
            ReactionEmoji.Custom custom = asCustom.get();
            if(custom.isAnimated()) {
                return "<a:" + custom.getName() + ":" + custom.getId().asString() + ">";
            } else {
                return "<" + custom.getName() + ":" + custom.getId().asString() + ">";
            }
        } else if(asUnicode.isPresent()) {
            return asUnicode.get().getRaw();
        } else {
            throw new BotException("Encountered react that was neither custom nor unicode");
        }
    }

    private String createMessage(String message, List<Poll.Choice> choices) {
        return "New Poll:\n" + message + "\n" + choices.stream().map(choice -> choice.getOption() + " " + choice.getMeaning()).collect(Collectors.joining("\n"));
    }
}
