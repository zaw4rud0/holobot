package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.database.dao.CountdownDao;
import dev.zawarudo.holo.modules.countdown.Countdown;
import dev.zawarudo.holo.utils.DateTimeUtils;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Deactivated
@CommandInfo(name = "countdown",
        description = "Create, view and remove countdowns.",
        usage = "WIP",
        alias = {"cd"},
        category = CommandCategory.MISC
)
public class CountdownCmd extends AbstractCommand {

    private final CountdownDao countdownDao;

    public CountdownCmd(CountdownDao countdownDao) {
        this.countdownDao = countdownDao;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0 || "list".equals(args[0])) {
            showList(event);
            return;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if ("add".equals(sub)) {
            // Expected: add <name> <date time...>
            if (args.length < 3) {
                String formatted = String.format("Usage: `%scountdown add <name> <date time>`", getPrefix(event));
                event.getMessage().reply(formatted).queue();
                return;
            }

            String name = args[1];
            String dateTime = joinFrom(args, 2);
            createCountdown(event, name, dateTime);
            return;
        }

        if ("remove".equals(sub) || "r".equals(sub)) {
            // Expected: remove <id>
            if (args.length < 2) {
                String formatted = String.format("Usage: `%scountdown remove <id>`", getPrefix(event));
                event.getMessage().reply(formatted).queue();
                return;
            }

            removeCountdown(event, args[1]);
            return;
        }

        showCountdown(event);
    }

    private void showCountdown(MessageReceivedEvent event) {
        try {
            long userId = event.getAuthor().getIdLong();
            long selectedId = Long.parseLong(args[0]);

            Optional<Countdown> selectedCountdown = countdownDao.findAllById(userId)
                    .stream()
                    .filter(cd -> cd.id() == selectedId)
                    .findFirst();

            if (selectedCountdown.isPresent()) {
                Countdown cd = selectedCountdown.get();

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Countdown Information");
                embedBuilder.addField("ID", String.valueOf(cd.id()), false);
                embedBuilder.addField("Name", cd.name(), false);
                embedBuilder.addField("Date", DateTimeUtils.formatDateTime(cd.dateTime()), false);
                embedBuilder.addField("Remaining Time", Formatter.getRelativeTime(cd.dateTime()), false);
                embedBuilder.addField("Time Created", DateTimeUtils.formatDateTime(cd.timeCreated()), false);

                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            } else {
                event.getMessage().reply("You don't have a countdown with the given ID! Please check your list and try again.").queue();
            }
        } catch (SQLException e) {
            logger.error("Something went wrong", e);
            event.getMessage().reply("Something went wrong while working with the database.").queue();
        } catch (NumberFormatException e) {
            event.getMessage().reply("Please enter a valid countdown ID!").queue();
        }
    }

    private void showList(MessageReceivedEvent event) {
        try {
            List<Countdown> countdowns = countdownDao.findAllById(event.getAuthor().getIdLong());
            StringBuilder sb = new StringBuilder();
            for (Countdown cd : countdowns) {
                sb.append("* ").append(String.format("**%s** ", cd.name())).append(String.format("`[ID: %d]`", cd.id())).append("\n")
                        .append(DateTimeUtils.formatDateTime(cd.dateTime())).append("\n")
                        .append(Formatter.getRelativeTime(cd.dateTime())).append("\n");
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Your Countdowns");
            String desc = sb.isEmpty() ? "Your list is empty." : sb.toString();
            embedBuilder.setDescription(desc);

            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        } catch (SQLException e) {
            logger.error("Something went wrong", e);
            event.getMessage().reply("Something went wrong while fetching your countdowns.").queue();
        }
    }

    private void createCountdown(MessageReceivedEvent event, String name, String input) {
        try {
            long created = System.currentTimeMillis();

            long millis = DateTimeUtils.parseDateTime(input);
            String dateTime = DateTimeUtils.formatDateTime(millis);

            Countdown countdown = new Countdown(-1, name, created, millis, event.getAuthor().getIdLong(), event.getGuild().getIdLong());
            countdownDao.insertIgnore(countdown);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Created countdown");
            embedBuilder.addField("Name", name, false);
            embedBuilder.addField("Date", dateTime, false);
            embedBuilder.addField("Remaining time", Formatter.getRelativeTime(millis), false);

            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        } catch (SQLException e) {
            logger.error("Something went wrong", e);
            event.getMessage().reply("Something went wrong while storing your countdown.").queue();
        } catch (IllegalArgumentException e) {
            event.getMessage().reply("I can't parse your given date and/or time! Make sure you didn't make a typo and try again.").queue();
        }
    }

    private void removeCountdown(MessageReceivedEvent event, String rawId) {
        try {
            long userId = event.getAuthor().getIdLong();
            long selectedId = Long.parseLong(rawId);

            Optional<Countdown> selectedCountdown = countdownDao.findAllById(userId)
                    .stream()
                    .filter(cd -> cd.id() == selectedId)
                    .findFirst();

            if (selectedCountdown.isPresent()) {
                countdownDao.deleteIgnore(selectedCountdown.get().id());
                event.getMessage().reply("Successfully removed your countdown.").queue();
            } else {
                event.getMessage().reply("You don't have a countdown with the given ID! Please check your list and try again.").queue();
            }
        } catch (SQLException e) {
            logger.error("Something went wrong", e);
            event.getMessage().reply("Something went wrong while working with the database.").queue();
        } catch (NumberFormatException e) {
            event.getMessage().reply("Please enter a valid countdown ID!").queue();
        }
    }

    private static String joinFrom(String[] args, int startIdx) {
        if (startIdx >= args.length) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = startIdx; i < args.length; i++) {
            if (i > startIdx) sb.append(" ");
            sb.append(args[i]);
        }
        return sb.toString().trim();
    }
}