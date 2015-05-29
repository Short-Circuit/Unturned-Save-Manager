package com.shortcircuit.unturnedsavemanager;

import java.io.File;

/**
 * @author ShortCircuit908
 */
public class FileWrapper {
	private final File file;

	public FileWrapper(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return file.getName();
	}
}
