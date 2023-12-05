package dev.zawarudo.holo.image;

import dev.zawarudo.aoc_utils.graph.AdventOfCodeGraph;
import dev.zawarudo.aoc_utils.graph.ChartType;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.utils.ImageOperations;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Command(name = "aoc",
        description = "Displays the graph of Advent of Code",
        category = CommandCategory.IMAGE)
public class AoCStatsCmd extends AbstractCommand {

    private static final int LEADERBOARD_ID = 1514956;
    private static final int YEAR = 2023;

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);
        sendTyping(event);

        String token = Bootstrap.holo.getConfig().getAoCToken();
        AdventOfCodeGraph graph = AdventOfCodeGraph.createGraph(ChartType.STACKED_BAR_CHART, YEAR, LEADERBOARD_ID, token);
        graph.setBackground(Color.decode("#0F0F23"));
        BufferedImage image = graph.generateImage();

        String name = String.format("aoc_%s.png", getCurrentDateTime());

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Advent of Code 2023 Stats");
        builder.setImage("attachment://" + name);
        builder.setFooter("Invoked by " + event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl());

        try (InputStream input = ImageOperations.toInputStream(image)) {
            FileUpload upload = FileUpload.fromData(input, name);
            event.getChannel().sendFiles(upload).setEmbeds(builder.build()).queue();
        } catch (IOException ignored) {
            // TODO: Properly handle exceptions
        }
    }

    private static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        return now.format(formatter);
    }
}