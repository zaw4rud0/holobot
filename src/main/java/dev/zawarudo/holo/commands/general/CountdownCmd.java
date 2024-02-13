package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.utils.DateTimeUtils;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "countdown",
        description = "Shows your countdowns.",
        alias = {"cd", "count"},
        category = CommandCategory.MISC
)
public class CountdownCmd extends AbstractCommand {

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
            removeCountdown(event);
        } else {
            event.getMessage().reply("Unsupported atm, sorry!").queue();
        }
    }

    private void showList(MessageReceivedEvent event) {
        try {
            List<Countdown> countdowns = fetchCountdowns(event.getAuthor().getIdLong());

            StringBuilder sb = new StringBuilder();

            for (Countdown cd : countdowns) {
                sb.append("* ").append(String.format("**%s** ", cd.name)).append(String.format("`[ID: %d]`", cd.id)).append("\n")
                        .append(DateTimeUtils.formatDateTime(cd.dateTime)).append("\n")
                        .append(getRelativeTime(cd.dateTime)).append("\n");
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Your Countdowns");
            embedBuilder.setDescription(sb.toString());

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

            try {
                Countdown countdown = new Countdown(-1, name, created, millis, event.getAuthor().getIdLong(), event.getGuild().getIdLong());
                store(countdown);
            } catch (SQLException e) {
                logger.error("Something went wrong", e);

                event.getMessage().reply("Something went wrong while storing your countdown.").queue();
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Created countdown");
            embedBuilder.addField("Name", name, false);
            embedBuilder.addField("Date", dateTime, false);
            embedBuilder.addField("Remaining time", getRelativeTime(millis), false);

            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        } catch (IllegalArgumentException e) {
            event.getMessage().reply("I can't parse your given date and/or time! Make sure you didn't make a typo and try again.").queue();
        }
    }

    private void removeCountdown(MessageReceivedEvent event) {
        // TODO:
        //  * Check if given id is correct
        //  * Remove from database
        //  * Success message
    }

    private String getRelativeTime(long millis) {
        long diff = millis - System.currentTimeMillis();

        boolean isFuture = diff > 0;
        String prefix = isFuture ? "In " : "";
        String suffix = isFuture ? "" : " ago";
        diff = Math.abs(diff);

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        diff -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        diff -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        diff -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

        StringBuilder sb = new StringBuilder(prefix);
        if (days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s" : "");
            if (hours > 0 || minutes > 0 || seconds > 0) sb.append(", ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hour").append(hours > 1 ? "s" : "");
            if (minutes > 0 || seconds > 0) sb.append(", ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
            if (seconds > 0) sb.append(", ");
        }
        if (seconds > 0) {
            sb.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
        }

        if (sb.length() == prefix.length()) {
            sb.append("less than a second");
        }

        sb.append(suffix);

        return Formatter.capitalize(sb.toString());
    }

    private void store(Countdown countdown) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-countdown");

        Connection conn = Database.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, countdown.name);
            ps.setLong(2, countdown.timeCreated);
            ps.setLong(3, countdown.dateTime);
            ps.setLong(4, countdown.userId);
            ps.setLong(5, countdown.serverId);

            ps.execute();
        }

        conn.close();
    }

    private List<Countdown> fetchCountdowns(long userId) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-countdown");

        Connection conn = Database.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            List<Countdown> countdowns = new ArrayList<>();

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                long timeCreated = rs.getLong("time_created");
                long dateTime = rs.getLong("date_time");
                long serverId = rs.getLong("guild_id");

                Countdown countdown = new Countdown(id, name, timeCreated, dateTime, userId, serverId);
                countdowns.add(countdown);
            }

            conn.close();
            return countdowns;
        }
    }

    private record Countdown(long id, String name, long timeCreated, long dateTime, long userId, long serverId) {}
}