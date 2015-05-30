package com.shortcircuit.unturnedsavemanager;

import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;
import com.shortcircuit.unturnedsavemanager.ui.MainWindow;

import org.apache.commons.lang3.SystemUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * @author ShortCircuit908
 */
public class Main {
	public static final Properties SETTINGS = new Properties();
	public static final String VERSION = "3.0.0";
	private static final String[] resources = {"CHANGELOG.txt", "LICENSE.txt", "README.txt", "USAGE.txt"};
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

	private static void setDefaultProps(){
		SETTINGS.setProperty("first_run", "true");
		SETTINGS.setProperty("compression_method", "BEST_COMPRESSION");
		String steam_path =
				(SystemUtils.IS_OS_WINDOWS
						? "/Program Files (x86)/Steam/"
						: SystemUtils.IS_OS_LINUX
						? "~/.local/share/Steam/"
						: SystemUtils.IS_OS_MAC
						? "~/Library/Application Support/Steam/"
						: "/").replace("/", FileSystems.getDefault().getSeparator());
		SETTINGS.setProperty("steam_path", steam_path);
		SETTINGS.setProperty("version", "0.0.0");
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				saveSettings();
			}
		}));
		setDefaultProps();
		loadSettings();
		copyResources();
		firstRun();
		new MainWindow();
	}

	private static void firstRun(){
		if(Desktop.isDesktopSupported()){
			if(!VERSION.equals(SETTINGS.getProperty("version"))) {
				try {
					Desktop.getDesktop().browse(new File("CHANGELOG.txt").toURI());
					SETTINGS.setProperty("version", VERSION);
					SETTINGS.setProperty("first_run", "true");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(Boolean.parseBoolean(SETTINGS.getProperty("first_run"))) {
				try {
					Desktop.getDesktop().browse(new File("USAGE.txt").toURI());
					SETTINGS.setProperty("first_run", "false");
				}
				catch (IOException e) {
					copyResources();
					e.printStackTrace();
				}

			}
		}
	}

	private static void copyResources() {
		for (String resource : resources) {
			try {
				Files.copy(Main.class.getResourceAsStream("resources/" + resource), new File(resource).toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadSettings() {
		try {
			File file = new File("settings.xml");
			if (file.exists()) {
				SETTINGS.loadFromXML(new FileInputStream(file));
			}
			else {
				saveSettings();
				loadSettings();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveSettings() {
		try {
			File file = new File("settings.xml");
			file.createNewFile();
			SETTINGS.storeToXML(new FileOutputStream(file), "Edit these settings at your own risk");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
