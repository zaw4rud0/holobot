package com.xharlock.holo.place;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

public abstract class PlaceCommand extends Command {

	public PlaceCommand(String name) {
		super(name);
		setCommandCategory(CommandCategory.PLACE);
	}

	protected BufferedImage getImageFromList(List<String> commands) {
		
		return null;
	}
	
	protected InputStream listToInputStream(List<String> commands) throws IOException {		
		File file = new File("./output/commands-" + commands.size() + "lines.txt");
		
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		
		for (String line : commands)
			pw.write(line + "\n");
		
		pw.close();		
		InputStream in = new FileInputStream(file);
		file.delete();
		
		return in;
	}
}
