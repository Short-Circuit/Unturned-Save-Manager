package com.shortcircuit.unturnedsavemanager;

import java.io.File;

/**
 * @author ShortCircuit908
 */
public class ZipFileWrapper extends FileWrapper {
	private final String zip_dir;
	public ZipFileWrapper(File parent, File file) {
		super(file);
		zip_dir = parent.getPath().split("Unturned\\\\")[1] + "\\" + file.getName();
		System.out.println(zip_dir);
	}
	public String getZipDir(){
		return zip_dir;
	}
}
