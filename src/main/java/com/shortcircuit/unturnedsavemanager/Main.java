package com.shortcircuit.unturnedsavemanager;

import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;

import java.io.File;
import java.io.FileFilter;

/**
 * @author ShortCircuit908
 */
public class Main {
	public static final String BASE_DIR = "/Program Files (x86)/Steam/steamapps/common/Unturned/Servers/%1$s/Level/%2$s";
	public static final FileFilter DIRECTORY_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};
	public static final FileFilter ZIP_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".zip");
		}
	};
	public static final Escaper FILEPATH_ESCAPER = new PercentEscaper(" ~!@#$%^&()_+`-={}[];\',.", false);

	public static void main(String... args) {
		new MainWindow();
	}
}
