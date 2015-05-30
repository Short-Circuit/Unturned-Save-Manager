package com.shortcircuit.unturnedsavemanager.zip;

import com.shortcircuit.unturnedsavemanager.structures.FileWrapper;

import java.io.File;

/**
 * @author ShortCircuit908
 */
public class ZipFileWrapper extends FileWrapper {
	private final String zip_dir;
	public ZipFileWrapper(File parent, File file) {
		super(file);
		zip_dir = parent.getPath().split("Unturned\\\\")[1] + "\\" + file.getName();
	}
	public String getZipDir(){
		return zip_dir;
	}
}
