package com.shortcircuit.unturnedsavemanager;

import java.io.File;

/**
 * @author ShortCircuit908
 */
public class Player {
	private final String id;
	private final File file;

	public Player(File file, String map_name) {
		this.id = file.getName();
		this.file = new File(file + "\\" + map_name + "\\Player");
	}

	public String getId(){
		return id;
	}

	public File getFile(){
		return file;
	}
}
