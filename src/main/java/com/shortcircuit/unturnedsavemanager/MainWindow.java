package com.shortcircuit.unturnedsavemanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * @author ShortCircuit908
 */
public class MainWindow {
	private JPanel content_panel;
	private JButton load_button;
	private JButton delete_button;
	private JList<String> save_list;
	private JButton save_button;
	private JTextField name_field;
	private JLabel icon_label;
	private javax.swing.JTree map_box;
	private JButton refresh_map_button;
	private JButton settings_button;
	private JCheckBox player_data;
	private JCheckBox server_data;
	private JButton refresh_save_button;
	public static final File SAVE_DIR = new File("saves");

	static {
		SAVE_DIR.mkdir();
	}

	public MainWindow() {
		JFrame frame = new JFrame("Unturned Save Manager");
		try {
			frame.setIconImage(ImageIO.read(Main.class.getResourceAsStream("icon.png")));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		frame.setContentPane(content_panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		refreshMaps();
		refreshSaves();
		map_box.setOpaque(false);
		map_box.setModel(new SimpleTreeModel());
		frame.setVisible(true);
		settings_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(getSteamDir().getParentFile());
				chooser.setSelectedFile(getSteamDir());
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("Select Steam directory");
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileView(new DirectoryFileView());
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					setSteamDir(file);
				}
			}
		});
		refresh_map_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkSteamDir()){
					return;
				}
				refreshMaps();
			}
		});
		save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkSteamDir()){
					return;
				}
				if (map_box.getSelectionPath() != null && map_box.getSelectionPath().getLastPathComponent() instanceof FileWrapper && !name_field.getText().trim().isEmpty()) {
					Server server = (Server) map_box.getSelectionPath().getPathComponent(1);
					String map_name = map_box.getSelectionPath().getLastPathComponent().toString();
					saveMap(server, map_name, Main.FILEPATH_ESCAPER.escape(name_field.getText().trim()), player_data.isSelected(), server_data.isSelected());
					refreshSaves();
				}
			}
		});
		load_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkSteamDir()){
					return;
				}
				if(save_list.getSelectedIndex() != -1) {
					File save_file = ((SimpleListModel) save_list.getModel()).getFile(save_list.getSelectedIndex());
					new ZipProgress(save_file, new File(getSteamDir() + "\\steamapps\\common\\Unturned"));
				}
			}
		});
		refresh_save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkSteamDir()){
					return;
				}
				refreshSaves();
			}
		});
		delete_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkSteamDir()){
					return;
				}
				if(save_list.getSelectedIndex() != -1){
					((SimpleListModel)save_list.getModel()).getFile(save_list.getSelectedIndex()).delete();
					refreshSaves();
				}
			}
		});
	}

	public static void setSteamDir(File steam_dir) {
		try {
			File store = new File("settings");
			store.createNewFile();
			FileWriter writer = new FileWriter(store);
			writer.write(steam_dir.getCanonicalPath());
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getSteamDir() {
		try {
			File store = new File("settings");
			Scanner scanner = new Scanner(store);
			if (scanner.hasNextLine()) {
				return new File(scanner.nextLine());
			}
		}
		catch (IOException e) {
			System.out.println(e.getClass().getCanonicalName() + ": " + e.getMessage());
		}
		return new File("/");
	}

	public static File[] listDirectories(File parent) {
		return parent.listFiles(Main.DIRECTORY_FILTER);
	}

	private void refreshMaps() {
		if(!checkSteamDir()){
			return;
		}
		File[] servers = listDirectories(new File(getSteamDir() + "\\steamapps\\common\\Unturned\\Servers"));
		ArrayList<Server> server_objects = new ArrayList<>();
		for (File server : servers) {
			server_objects.add(new Server(server));
		}
		map_box.setModel(new SimpleTreeModel(server_objects.toArray(new Server[0])));
	}

	public static void saveMap(Server server, String map_name, String save_name, boolean include_player, boolean include_server) {
		if(!checkSteamDir()){
			return;
		}
		ArrayList<ZipFileWrapper> files = new ArrayList<>();
		final String base_dir = getSteamDir() + "\\steamapps\\common\\Unturned\\Servers\\" + server.getName();
		File file = new File(base_dir + "\\Level\\" + map_name);
		files.add(new ZipFileWrapper(server.getMapFile(), file));
		if (include_player) {
			for (Player player : server.getPlayerData(map_name)) {
				System.out.println(player.getId());
				files.add(new ZipFileWrapper(new File(server.getPlayerFile() + "\\" + player.getId() + "\\" + map_name), player.getFile()));
			}
		}
		if(include_server){
			files.add(new ZipFileWrapper(server.getFile(), server.getServerFile()));
		}
		try {
			File save = new File(SAVE_DIR + "\\" + save_name + " (" + server.getName() + "#" + map_name + ").zip");
			System.out.println(save);
			save.createNewFile();
			new ZipProgress(save, files.toArray(new ZipFileWrapper[0]));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refreshSaves(){
		save_list.setModel(new SimpleListModel(SAVE_DIR.listFiles(Main.ZIP_FILTER)));
	}

	private static boolean checkSteamDir(){
		if(!getSteamDir().getName().equals("Steam")){
			new ConfigDialog();
			return false;
		}
		return true;
	}
}
