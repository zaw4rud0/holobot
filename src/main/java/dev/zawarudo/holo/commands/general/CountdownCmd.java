package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.utils.DateTimeUtils;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Command(name = "countdown",
        description = "Shows your countdowns.",
        alias = {"cd"},
        category = CommandCategory.MISC
)
public class CountdownCmd extends AbstractCommand {

    /**
     * Represents a countdown instance.
     *
     * @param id          The id of the countdown in the database.
     * @param name        The name of the countdown given by the user.
     * @param timeCreated The time of the creation of the countdown.
     * @param dateTime    The exact date and time the countdown points to.
     * @param userId      The id of the user who created the countdown.
     * @param serverId    The id of the server the countdown was created in. It will
     *                    be used later to define global and server-only countdowns.
     */
    public record Countdown(long id, String name, long timeCreated, long dateTime, long userId, long serverId) {
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0 || args[0].equals("list")) {
            showList(event);
        } else if (args[0].equals("add")) {
            args = Arrays.copyOfRange(args, 1, args.length);
            String name = args[0];
            String dateTime = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            createCountdown(event, name, dateTime);
        } else if (args[0].equals("remove") || args[0].equals("r")) {
            args = Arrays.copyOfRange(args, 1, args.length);
            removeCountdown(event);
        } else {
            showCountdown(event);
        }
    }

    private void showCountdown(MessageReceivedEvent event) {
        try {
            long userId = event.getAuthor().getIdLong();
            long selectedId = Long.parseLong(args[0]);

            Optional<Countdown> selectedCountdown = DBOperations.fetchCountdowns(userId)
                    .stream()
                    .filter(cd -> cd.id == selectedId)
                    .findFirst();

            if (selectedCountdown.isPresent()) {
                Countdown cd = selectedCountdown.get();

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Countdown Information");
                embedBuilder.addField("ID", String.valueOf(cd.id), false);
                embedBuilder.addField("Name", cd.name, false);
                embedBuilder.addField("Date", DateTimeUtils.formatDateTime(cd.dateTime), false);
                embedBuilder.addField("Remaining Time", DateTimeUtils.getRelativeTime(cd.dateTime), false);
                embedBuilder.addField("Time Created", DateTimeUtils.formatDateTime(cd.timeCreated), false);

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
            List<Countdown> countdowns = DBOperations.fetchCountdowns(event.getAuthor().getIdLong());
            StringBuilder sb = new StringBuilder();
            for (Countdown cd : countdowns) {
                sb.append("* ").append(String.format("**%s** ", cd.name)).append(String.format("`[ID: %d]`", cd.id)).append("\n")
                        .append(DateTimeUtils.formatDateTime(cd.dateTime)).append("\n")
                        .append(DateTimeUtils.getRelativeTime(cd.dateTime)).append("\n");
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
            DBOperations.insertCountdown(countdown);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Created countdown");
            embedBuilder.addField("Name", name, false);
            embedBuilder.addField("Date", dateTime, false);
            embedBuilder.addField("Remaining time", DateTimeUtils.getRelativeTime(millis), false);

            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        } catch (SQLException e) {
            logger.error("Something went wrong", e);
            event.getMessage().reply("Something went wrong while storing your countdown.").queue();
        } catch (IllegalArgumentException e) {
            event.getMessage().reply("I can't parse your given date and/or time! Make sure you didn't make a typo and try again.").queue();
        }
    }

    private void removeCountdown(MessageReceivedEvent event) {
        try {
            long userId = event.getAuthor().getIdLong();
            long selectedId = Long.parseLong(args[0]);

            Optional<Countdown> selectedCountdown = DBOperations.fetchCountdowns(userId)
                    .stream()
                    .filter(cd -> cd.id == selectedId)
                    .findFirst();

            if (selectedCountdown.isPresent()) {
                DBOperations.deleteCountdown(selectedCountdown.get().id);
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
}