package com.xharlock.holo.commands.cmds;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.database.DatabaseOPs;
import com.xharlock.holo.utils.Formatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ExamCmd extends Command {

	public static LinkedHashMap<String, Long> exam_dates;

	public ExamCmd(String name) {
		super(name);
		setDescription("Use this command to display the exam dates and their countdowns.");
		setUsage(name);
		setCommandCategory(CommandCategory.GENERAL);

		try {
			exam_dates = DatabaseOPs.getExamDates();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();

		EmbedBuilder builder = new EmbedBuilder();

		// Owner can add an exam date (Format: DD-MM-YYYY HH:MM)
		if (args.length == 4 && args[0].equals("add") && isBotOwner(e)) {
			String date_time = args[2] + " " + args[3];
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
			Date date = null;

			try {
				date = sdf.parse(date_time);				
				long millis = date.getTime();
				DatabaseOPs.insertExamDate(args[1], millis);
				exam_dates.put(args[1], millis);
			} catch (ParseException | SQLException ex) {
				ex.printStackTrace();
			}

			builder.setTitle("Exam added to the database");
			builder.setDescription("**Name:** " + args[1] + "\n" + "**Date:** " + date_time);

			sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
			return;
		}

		long now = System.currentTimeMillis();

		builder.setTitle("Exams");

		if (exam_dates.keySet().size() == 0) {
			builder.setDescription("All exams are over!");
		}
		
		// Iterate through map
		for (String exam : exam_dates.keySet()) {
			long millis = exam_dates.get(exam);
			long remaining = millis - now;

			String title = exam + " (" + Formatter.formatDateTime(millis) + ")";

			// Don't display exams older than 1 week
			if (remaining < -1 * TimeUnit.DAYS.toMillis(7)) {
				continue;
			}
			
			// Exam is over
			else if (remaining < 0){
				builder.addField(title, "Survived", false);	
			}
			
			// Still a bit of time left until exam
			else {
				String countdown = Formatter.formatTime(remaining);
				builder.addField(title, countdown, false);
			}
		}
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}
