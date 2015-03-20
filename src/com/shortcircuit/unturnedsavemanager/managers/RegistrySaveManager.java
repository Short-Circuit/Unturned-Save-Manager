package com.shortcircuit.unturnedsavemanager.managers;

import com.shortcircuit.unturnedsavemanager.UnturnedSaveManager;
import com.shortcircuit.unturnedsavemanager.registry.WinRegistry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * @author ShortCircuit908
 */
public class RegistrySaveManager {
	public RegistrySaveManager() {

	}

	public void save(String name, boolean save_players) {
		try {
			String data = "Windows Registry Editor Version 5.00\n\n[HKEY_CURRENT_USER\\Software\\Smartly "
					+ "Dressed Games\\Unturned]\n";
			String key_string = "MapName barricades structures vehicles";
			if(save_players) {
				key_string += " inventory last clothes skills life position";
			}
			WinRegistry.deleteKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Smartly Dressed Games\\Unturned");
			WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Smartly Dressed Games\\Unturned");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Smartly Dressed Games\\Unturned", "MapName", name);
			String[] keys = key_string.split(" ");
			for(String key : keys) {
				List<String> reg_keys = getMatchingKeys(key);
				for(String reg_key : reg_keys) {
					String reg_entry = "\"" + WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER,
							"Software\\Smartly Dressed Games\\Unturned", reg_key) + "\"";
					if(reg_entry.equals("\"null\"")) {
						Process proc = Runtime.getRuntime().exec("REG QUERY \"HKEY_CURRENT_USER\\"
								+ "Software\\Smartly Dressed Games\\Unturned\" /v " + reg_key + " /z");
						Scanner scan = new Scanner(proc.getInputStream());
						scan.nextLine();
						scan.nextLine();
						String[] reg_values = scan.nextLine().trim().split("    ");
						proc.waitFor();
						scan.close();
						proc.destroy();
						if(reg_values.length == 3) {
							reg_entry = reg_values[2];
						}
					}
					data += "\"" + reg_key + "\"=" + reg_entry + "\n";
				}
			}
			composeReg(name, data);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void composeReg(String name, String data) {
		try {
			File save = new File(UnturnedSaveManager.SAVE_DIR + "/" + name + ".reg");
			save.createNewFile();
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(save),
					"windows-1252"));
			out.write(data);
			out.flush();
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void importReg(File save) {
		if(save.exists()) {
			String path = save.getAbsolutePath();
			try {
				Process proc = Runtime.getRuntime().exec("REG IMPORT \"" + path + "\"");
				proc.waitFor();
				proc.destroy();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void resetMap() {
		String[] keys = {"MapName", "barricades", "structures", "vehicles", "inventory", "last", "clothes",
				"skills", "life", "position"};
		for(String key : keys) {
			List<String> reg_keys = getMatchingKeys(key);
			for(String reg_key : reg_keys) {
				try {
					String dir = "\"HKEY_CURRENT_USER\\Software\\Smartly Dressed Games\\Unturned\" /v "
							+ reg_key + " /f";
					Process proc = Runtime.getRuntime().exec("REG DELETE " + dir);
					proc.waitFor();
					proc.destroy();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<String> getMatchingKeys(String prefix) {
		List<String> results = new ArrayList<>();
		try {
			String dir = "\"HKEY_CURRENT_USER\\Software\\Smartly Dressed Games\\Unturned\"";
			Process proc = Runtime.getRuntime().exec("REG QUERY " + dir);
			Scanner scan = new Scanner(proc.getInputStream());
			while(scan.hasNextLine()) {
				String line = scan.nextLine().trim();
				if(!line.isEmpty() && line.startsWith(prefix) && !line.equals(dir.replace("\"", ""))) {
					results.add(line.split("    ")[0]);
				}
			}
			proc.waitFor();
			scan.close();
			proc.destroy();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public void backUpWorld(String name) {
		try {
			String file_name = new File(UnturnedSaveManager.SAVE_DIR + "/" + name + ".reg.bak").getAbsolutePath();
			Process p = Runtime.getRuntime().exec("REG EXPORT \"HKEY_CURRENT_USER\\Software\\Smartly Dressed Games\\Unturned\" " + file_name);
			p.waitFor();
			p.destroy();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void restoreBackup(String name) {
		try {
			String file_name = UnturnedSaveManager.SAVE_DIR + "/" + name + ".reg.bak";
			Process p = Runtime.getRuntime().exec("REG IMPORT " + file_name);
			p.waitFor();
			p.destroy();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
