package com.shortcircuit.unturnedsavemanager;

import com.shortcircuit.unturnedsavemanager.managers.RegistrySaveManager;
import com.shortcircuit.unturnedsavemanager.objects.MapSave;
import com.shortcircuit.unturnedsavemanager.registry.WinRegistry;
import com.shortcircuit.unturnedsavemanager.swing.Confirmation;
import com.shortcircuit.unturnedsavemanager.swing.InputConfirmation;
import com.shortcircuit.unturnedsavemanager.swing.MapListModel;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author ShortCircuit908
 */
public class UnturnedSaveManager {
	private JFrame frame;
	private JLabel current_map;
	private JButton button_export;
	private JButton button_backup;
	private JButton button_reset;
	private JList<MapSave> map_list;
	private JPanel content_panel;
	private JButton button_load;
	private JButton button_delete;
	private JButton button_restore;
	private final RegistrySaveManager save_manager = new RegistrySaveManager();
	private static UnturnedSaveManager manager;
	private static Method LOAD_MAP;
	private static Method DELETE_MAP;
	private static Method SAVE_MAP;
	private static Method RESET_MAP;
	private static Method CONFIRM_OVERWRITE;
	private static Method RESTORE_BACKUP;
	public static final File SAVE_DIR = new File("Saves");
	private final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'_'HH-mm-ss");
	static{
		SAVE_DIR.mkdirs();
		try{
			LOAD_MAP = UnturnedSaveManager.class.getDeclaredMethod("loadMap");
			DELETE_MAP = UnturnedSaveManager.class.getDeclaredMethod("deleteMap");
			SAVE_MAP = UnturnedSaveManager.class.getDeclaredMethod("saveMap", String.class, boolean.class);
			RESET_MAP = UnturnedSaveManager.class.getDeclaredMethod("resetMap");
			CONFIRM_OVERWRITE = UnturnedSaveManager.class.getDeclaredMethod("confirmOverwrite", String.class, boolean.class);
			RESTORE_BACKUP = UnturnedSaveManager.class.getDeclaredMethod("restoreBackup");
		}
		catch(ReflectiveOperationException e){
			e.printStackTrace();
		}
	}

	public static void main(String... args) {
		manager = new UnturnedSaveManager();
	}

	public UnturnedSaveManager() {
		frame = new JFrame("Unturned Save Manager");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(UnturnedSaveManager.class
				.getResource("/com/shortcircuit/unturnedsavemanager/resources/icon.png")));
		frame.setContentPane(content_panel);
		final MapListModel model = new MapListModel();
		map_list.setModel(model);
		new Thread(model).start();
		updateMapName();
		map_list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean selected = e.getFirstIndex() != -1;
				button_delete.setEnabled(selected);
				button_load.setEnabled(selected);
				button_restore.setEnabled(selected && map_list.getSelectedValue().isBackup());
			}
		});
		/*
		map_list.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				button_delete.setEnabled(false);
				button_load.setEnabled(false);
				map_list.setSelectedIndices(new int[]{});
			}
		});
		*/
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				model.halt();
				frame.dispose();
			}
		});
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		button_load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Confirmation(manager, frame.getLocationOnScreen(), frame.getIconImage(), "Load map", "Overwrite the current map?", LOAD_MAP, null);
			}
		});
		button_delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Confirmation(manager, frame.getLocationOnScreen(), frame.getIconImage(), "Delete map", "Delete this map?", DELETE_MAP, null);
			}
		});
		button_backup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createBackup();
			}
		});
		button_export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new InputConfirmation(manager, frame.getLocationOnScreen(), frame.getIconImage(),
						"Save as", CONFIRM_OVERWRITE);
			}
		});
		button_reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Confirmation(manager, frame.getLocationOnScreen(), frame.getIconImage(), "Reset map",
						"Reset current map?", RESET_MAP, null);
			}
		});
		button_restore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Confirmation(manager, frame.getLocationOnScreen(), frame.getIconImage(),
						"Restore backup", "Overwrite the current map?", RESTORE_BACKUP, null);
			}
		});
		saveDefaults();
	}

	public void updateMapName() {
		String map_name = "Unnamed";
		try {
			map_name = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "Software\\Smartly "
					+ "Dressed Games\\Unturned", "MapName");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(map_name != null) {
			current_map.setText(map_name);
		}
		else{
			current_map.setText("Unnamed");
		}
	}

	private void loadMap(){
		save_manager.importReg(map_list.getSelectedValue().getFile());
		updateMapName();
	}

	private void deleteMap(){
		for(MapSave map : map_list.getSelectedValuesList()) {
			map.getFile().delete();
		}
	}

	private void createBackup(){
		save_manager.backUpWorld(date_format.format(new Date()));
	}

	private void saveMap(String map_name, boolean save_players){
		save_manager.save(map_name, save_players);
	}

	private void resetMap(){
		save_manager.resetMap();
		updateMapName();
	}

	private void restoreBackup(){
		save_manager.restoreBackup(map_list.getSelectedValue().getName());
		updateMapName();
	}

	private void confirmOverwrite(String map_name, boolean save_players) {
		File file = new File(SAVE_DIR + "/" + map_name + ".reg");
		if(file.exists()) {
			new Confirmation(manager, frame.getLocationOnScreen(), frame.getIconImage(), "Export map",
					"Overwrite save file?", SAVE_MAP, null, map_name, save_players);
		}
		else{
			saveMap(map_name, save_players);
		}
	}

	private void saveDefaults(){
		try {
			Files.copy(UnturnedSaveManager.class.getResource("/com/shortcircuit/unturnedsavemanager/" +
					"resources/LICENSE.txt").openStream(), new File("LICENSE.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(UnturnedSaveManager.class.getResource("/com/shortcircuit/unturnedsavemanager/" +
					"resources/CHANGELOG.txt").openStream(), new File("CHANGELOG.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(UnturnedSaveManager.class.getResource("/com/shortcircuit/unturnedsavemanager/" +
					"resources/README.txt").openStream(), new File("README.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(UnturnedSaveManager.class.getResource("/com/shortcircuit/unturnedsavemanager/" +
					"resources/USAGE.txt").openStream(), new File("USAGE.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
