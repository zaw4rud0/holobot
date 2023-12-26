package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.apis.GitHubClient;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.Submission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Command(name = "bug",
        description = "Use this command to report a bug. Please provide a description of the bug and how it happened.",
        usage = "<text>",
        example = "Something went wrong",
        category = CommandCategory.GENERAL)
public class BugCmd extends AbstractCommand {

    private final GitHubClient githubClient;

    public BugCmd(GitHubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);
        EmbedBuilder eb = new EmbedBuilder();

        if (args.length == 0) {
            eb.setTitle("Incorrect Usage");
            eb.setDescription("Please provide a description of the bug");
            sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
            return;
        }

        String url;

        try {
            Submission submission = new Submission("Bug", event, String.join(" ", args));
            url = githubClient.createIssue(submission);
        } catch (IOException ex) {
            eb.setTitle("Error");
            eb.setDescription("An error occurred while creating a GitHub ticket! Please try again later.");
            sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
            return;
        }

        logger.info("Created a GitHub issue: {}", url);

        eb.setTitle("Bug Report Submitted");
        eb.setDescription("Thank you for reporting this bug! We will review it as soon as possible.");
        sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
    }
}