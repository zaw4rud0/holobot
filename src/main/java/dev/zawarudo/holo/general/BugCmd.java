package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.misc.Submission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Command(name = "bug",
        description = "Use this command to report a bug. Please provide a description of the bug and how it happened.",
        usage = "<text>",
        example = "Something went wrong",
        category = CommandCategory.GENERAL)
public class BugCmd extends AbstractCommand {

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

        try {
            Submission submission = new Submission(Submission.Type.BUG, event, String.join(" ", args));
            DBOperations.insertSubmission(submission);
        } catch (SQLException ex) {
            eb.setTitle("Error");
            eb.setDescription("An error occurred while connecting to the database!");
            sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
            return;
        }

        eb.setTitle("Bug Report Submitted");
        eb.setDescription("Thank you for reporting this bug! We will review it as soon as possible.");
        sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
    }
}