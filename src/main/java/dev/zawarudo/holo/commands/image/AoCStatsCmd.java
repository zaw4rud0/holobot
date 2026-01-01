package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.modules.aoc.graph.AdventOfCodeGraph;
import dev.zawarudo.holo.modules.aoc.graph.ChartType;
import dev.zawarudo.holo.utils.DateTimeUtils;
import dev.zawarudo.holo.utils.ImageOperations;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.utils.exceptions.APIException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@CommandInfo(name = "aoc",
        description = "Displays the graph of Advent of Code",
        category = CommandCategory.IMAGE)
public class AoCStatsCmd extends AbstractCommand {

    private static final int LEADERBOARD_ID = 1501119;

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);
        sendTyping(event);

        int year = getYear();

        String token = Bootstrap.holo.getConfig().getAocToken();
        AdventOfCodeGraph graph = AdventOfCodeGraph.createGraph(ChartType.STACKED_BAR_CHART, year, LEADERBOARD_ID, token);

        BufferedImage image;

        try {
            image = graph.generateImage();
        } catch (APIException ex) {
            sendErrorEmbed(event, "Something went wrong while fetching the AOC data. Please try again later.");
            return;
        }

        String name = String.format("aoc_%s.png", DateTimeUtils.getCurrentDateTimeString());

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("Advent of Code %d Stats", year));
        builder.setImage("attachment://" + name);
        builder.setFooter("Invoked by " + event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl());

        try (InputStream input = ImageOperations.toInputStream(image)) {
            FileUpload upload = FileUpload.fromData(input, name);
            event.getChannel().sendFiles(upload).setEmbeds(builder.build()).queue();
        } catch (IOException ex) {
            sendErrorEmbed(event, "An error occurred while sending the image. Please try again later.");
            ex.printStackTrace();
        }
    }

    private int getYear() {
        ZonedDateTime current = ZonedDateTime.now(ZoneId.of("Europe/Zurich"));
        int currentYear = current.getYear();

        if (args.length > 0) {
            try {
                int parsedYear = Integer.parseInt(args[0]);
                return (parsedYear >= 2015 && parsedYear <= currentYear) ? parsedYear : currentYear;
            } catch (NumberFormatException ignored) {
                // Fall through to default year logic
            }
        }

        return current.getMonthValue() == 12
                ? current.getYear()
                : current.getYear() - 1;
    }
}