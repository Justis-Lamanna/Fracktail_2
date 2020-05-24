package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.Poll;
import com.github.lucbui.bot.services.channel.BotChannelService;
import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.exception.CommandValidationException;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Commands
public class PollCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(PollCommands.class);

    @Autowired
    private DiscordClient bot;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private TranslateService translateService;

    @Command
    @CommandParams(value = 2, comparison = ParamsComparison.OR_MORE)
    @Timeout(value = 10, unit = ChronoUnit.MINUTES)
    public Mono<Void> startpoll(MessageCreateEvent event, @Param(0) String question, @Param(1) String duration, @Params(start = 2) String[] choices) {
        if(choices.length < 2) {
            throw translateService.getStringException("startpoll.usage");
        }

        return Mono.justOrEmpty(event.getGuildId())
                .flatMap(snowflake -> event.getMessage().getChannel())
                .zipWith(Mono.fromSupplier(() -> createPoll(question, duration, choices)))
                .flatMap(tuple -> tuple.getT1().createMessage(translateService.getFormattedString("job.poll.start",
                        tuple.getT2().getMessage(),
                        createChoicesList(tuple.getT2().getChoices()),
                        TranslateHelper.toDate( tuple.getT2().getExpiryTime())))
                        .zipWith(Mono.just(tuple.getT2()))
                )
                .doOnNext(tuple -> registerPoll(
                        tuple.getT1().getChannelId(),
                        tuple.getT1().getId(),
                        event.getMember().map(Member::getId).orElseThrow(() -> new BotException("Huh!")),
                        tuple.getT2()))
                .then();
    }

    private Poll createPoll(String question, String duration, String[] choices) {
        List<Poll.Choice> choicesList = Arrays.stream(choices)
                .map(str -> str.split(":"))
                .map(token -> {
                    if(token.length != 2){
                        throw translateService.getStringException("startpoll.usage");
                    }
                    return new Poll.Choice(token[0], token[1]);
                })
                .collect(Collectors.toList());
        return new Poll(question, determineEndtime(duration), choicesList);
    }

    private Instant determineEndtime(String endTimeStr) {
        if(endTimeStr.startsWith("P")){
            return Instant.now().plus(Duration.parse(endTimeStr));
        } else {
            return Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(endTimeStr));
        }
    }

    private void registerPoll(Snowflake channelId, Snowflake id, Snowflake reporter, Poll poll) {
        taskScheduler.schedule(() -> {
            Flux.fromIterable(poll.getChoices())
                    .flatMap(choice -> bot.getMessageById(channelId, id)
                            .map(msg -> getCountForChoice(choice, msg.getReactions()))
                            .map(count -> Tuples.of(choice, count)))
                    .collectSortedList(Collections.reverseOrder(Comparator.comparingInt(Tuple2::getT2)))
                    .doOnNext(results -> LOGGER.debug("Poll results: {}", results))
                    .doOnNext(results -> bot.getUserById(reporter)
                            .doOnNext(user -> LOGGER.debug("Sending results to {}", user.getUsername()))
                            .flatMap(User::getPrivateChannel)
                            .flatMap(channel -> channel.createMessage(translateService.getFormattedString("job.poll.results", poll.getMessage(), createChoiceAndResultsList(results))))
                            .onErrorResume(DiscordUtils.ON_FORBIDDEN, e -> Mono.empty())
                            .block()
                    )
                    .doOnNext(results -> bot.getChannelById(channelId)
                            .cast(TextChannel.class)
                            .flatMap(tc -> {
                                if(results.get(0).getT2() > 0) {
                                    return tc.createMessage(translateService.getFormattedString("job.poll.winner", results.get(0).getT1().getMeaning()));
                                } else {
                                    return tc.createMessage(translateService.getString("job.poll.noVotes"));
                                }
                            })
                            .then(bot.getMessageById(channelId, id))
                            .flatMap(msg -> msg.delete("Poll ended"))
                            .block()
                    )
                    .block();
        }, poll.getExpiryTime());

        LOGGER.debug("Scheduled poll to end at {}", poll.getExpiryTime());
    }

    private int getCountForChoice(Poll.Choice choice, Set<Reaction> reactions) {
        return reactions.stream().filter(reaction -> choice.getOption().equals(DiscordUtils.getEmojiString(reaction)))
                .findFirst()
                .map(Reaction::getCount)
                .orElse(0);
    }

    private String createChoicesList(List<Poll.Choice> choices) {
        return choices.stream()
                .map(choice -> translateService.getFormattedString("job.poll.choice", choice.getOption(), choice.getMeaning()))
                .collect(Collectors.joining("\n"));
    }

    private String createChoiceAndResultsList(List<Tuple2<Poll.Choice, Integer>> choices) {
        return choices.stream()
                .map(choice -> translateService.getFormattedString("job.poll.result", choice.getT1().getOption(), choice.getT1().getMeaning(), choice.getT2()))
                .collect(Collectors.joining("\n"));
    }
}
