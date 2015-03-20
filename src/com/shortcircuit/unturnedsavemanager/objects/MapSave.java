package com.shortcircuit.unturnedsavemanager.objects;

import com.shortcircuit.unturnedsavemanager.UnturnedSaveManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author ShortCircuit908
 */
public class MapSave {
	private final File file;
	private String name;

	public MapSave(File file) {
		this.file = file;
		this.name = file.getName().split("\\.")[0];
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBackup() {
		return file.getName().endsWith(".bak");
	}

	public void rename(String new_name) {
		try {
			String data = "";
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(line.startsWith("\"MapName\"")){
					line = "\"MapName\"=\"" + new_name + "\"";
				}
				data += line + "\n";
			}
			scanner.close();
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.flush();
			writer.close();
			file.renameTo(new File(UnturnedSaveManager.SAVE_DIR + "/" +  new_name + "."
					+ file.getName().split("\\.", 2)[1]));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return name + (isBackup() ? " (Backup)" : "");
	}
}
