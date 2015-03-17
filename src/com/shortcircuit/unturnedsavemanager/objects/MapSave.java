package com.shortcircuit.unturnedsavemanager.objects;

import java.io.File;

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

	public File getFile(){
		return file;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public boolean isBackup(){
		return file.getName().endsWith(".bak");
	}

	@Override
	public String toString(){
		return name + (isBackup() ? " (Backup)" : "");
	}
}
