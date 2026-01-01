package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.modules.GitHubClient;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.Submission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@CommandInfo(name = "suggestion",
        description = "Use this command if you want to suggest a feature. Suggestions are always appreciated.",
        usage = "<text>",
        example = "Make this bot more awesome <3",
        category = CommandCategory.GENERAL)
public class SuggestionCmd extends AbstractCommand {

    private final GitHubClient githubClient;

    public SuggestionCmd(GitHubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);
        EmbedBuilder eb = new EmbedBuilder();

        if (args.length == 0) {
            eb.setTitle("Incorrect Usage");
            eb.setDescription("Please provide a description of your suggestion");
            sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
            return;
        }

        String url;

        try {
            Submission submission = new Submission("Suggestion", event, String.join(" ", args));
            url = githubClient.createIssue(submission);
        } catch (IOException ex) {
            eb.setTitle("Error");
            eb.setDescription("An error occurred while creating a GitHub ticket! Please try again later.");
            sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
            return;
        }

        logger.info("Created a GitHub issue: {}", url);

        eb.setTitle("Suggestion Submitted");
        eb.setDescription("Thank you for your suggestion!");
        sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
    }
}