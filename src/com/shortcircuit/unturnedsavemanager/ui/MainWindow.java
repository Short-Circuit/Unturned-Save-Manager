package com.shortcircuit.unturnedsavemanager.ui;

import com.shortcircuit.unturnedsavemanager.Main;
import com.shortcircuit.unturnedsavemanager.structures.FileWrapper;
import com.shortcircuit.unturnedsavemanager.structures.Player;
import com.shortcircuit.unturnedsavemanager.structures.ReturnCallback;
import com.shortcircuit.unturnedsavemanager.structures.Server;
import com.shortcircuit.unturnedsavemanager.zip.ZipFileWrapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * @author ShortCircuit908
 */
public class MainWindow {
	private JPanel content_panel;
	private JButton load_button;
	private JButton delete_button;
	private JList<String> map_list;
	private JButton save_button;
	private JTextField name_field;
	private JLabel icon_label;
	private javax.swing.JTree map_box;
	private JButton refresh_map_button;
	private JButton settings_button;
	private JCheckBox player_data;
	private JCheckBox server_data;
	private JButton refresh_save_button;
	private JButton rename_button;
	public static final File SAVE_DIR = new File("saves");

	static {
		SAVE_DIR.mkdir();
	}

	public MainWindow() {
		JFrame frame = new JFrame("Unturned Save Manager v" + Main.VERSION);
		try {
			frame.setIconImage(ImageIO.read(Main.class.getResourceAsStream("resources/icon.png")));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		map_box.setOpaque(false);
		map_box.setModel(new MapTreeModel());
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
				if (!checkSteamDir()) {
					return;
				}
				refreshMaps();
			}
		});
		save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkSteamDir()) {
					return;
				}
				if (map_box.getSelectionPath() != null && map_box.getSelectionPath().getLastPathComponent() instanceof FileWrapper && !name_field.getText().trim().isEmpty()) {
					Server server = (Server) map_box.getSelectionPath().getPathComponent(1);
					String map_name = map_box.getSelectionPath().getLastPathComponent().toString();
					saveMap(server, map_name, Main.FILEPATH_ESCAPER.escape(name_field.getText().trim()), player_data.isSelected(), server_data.isSelected());
					refreshSaves();
					name_field.setText("");
					save_button.setEnabled(false);
				}
			}
		});
		load_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkSteamDir() || map_list.getSelectedIndices().length == 0) {
					return;
				}
				for (int index : map_list.getSelectedIndices()) {
					ReturnCallback<Boolean> confirmed = new ReturnCallback<>(false);
					new ConfirmationDialog("The map corresponding to \"" + map_list.getSelectedValue() + "\" will be overwritten. Continue?", confirmed);
					if (!confirmed.getValue()) {
						continue;
					}
					File save_file = ((MapListModel) map_list.getModel()).getFile(index);
					new ZipProgress(save_file, new File(getSteamDir() + "\\steamapps\\common\\Unturned"));
				}
			}
		});
		refresh_save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkSteamDir()) {
					return;
				}
				refreshSaves();
			}
		});
		delete_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkSteamDir() || map_list.getSelectedIndices().length == 0) {
					return;
				}
				for (int index : map_list.getSelectedIndices()) {
					ReturnCallback<Boolean> confirmed = new ReturnCallback<>(false);
					new ConfirmationDialog("Save \"" + map_list.getModel().getElementAt(index) + "\" will be permanently deleted. Continue?", confirmed);
					if (!confirmed.getValue()) {
						return;
					}
					((MapListModel) map_list.getModel()).getFile(index).delete();
				}
				refreshSaves();
			}
		});
		map_list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getFirstIndex() != -1 && e.getLastIndex() != -1) {
					load_button.setEnabled(true);
					delete_button.setEnabled(true);
					rename_button.setEnabled(true);
				}
				else {
					load_button.setEnabled(false);
					delete_button.setEnabled(false);
					rename_button.setEnabled(false);
				}
			}
		});
		rename_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (map_list.getSelectedIndices().length == 0) {
					return;
				}
				for (int index : map_list.getSelectedIndices()) {
					ReturnCallback<String> name = new ReturnCallback<>(null);
					new NameInputDialog("Enter a new name for \"" + map_list.getModel().getElementAt(index) + "\"", name);
					String new_name = name.getValue();
					if (new_name == null) {
						continue;
					}
					File file = ((MapListModel) map_list.getModel()).getFile(index);
					if (new_name.lastIndexOf(".zip") != -1) {
						new_name = new_name.substring(0, new_name.lastIndexOf(".zip"));
					}
					String filename = file.getName();
					int begin_index = filename.substring(0, filename.lastIndexOf('#')).lastIndexOf('(');
					String append = filename.substring(begin_index, filename.length());
					if (!file.renameTo(new File(file.getParent() + "\\" + Main.FILEPATH_ESCAPER.escape(new_name) + " " + append))) {
						ReturnCallback<Boolean> confirmed = new ReturnCallback<>(false);
						new ConfirmationDialog("Could not rename \"" + map_list.getModel().getElementAt(index) + "\"", confirmed);
					}
				}
				refreshSaves();
			}
		});
		map_box.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getLastPathComponent() instanceof FileWrapper) {
					name_field.setEnabled(true);
					server_data.setEnabled(true);
					player_data.setEnabled(true);
				}
				else {
					name_field.setEnabled(false);
					server_data.setEnabled(false);
					player_data.setEnabled(false);
				}
			}
		});
		name_field.setVerifyInputWhenFocusTarget(true);
		name_field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE){
					String modified = name_field.getText().trim().isEmpty() ? "" : name_field.getText().substring(0, name_field.getCaretPosition() - 1) + name_field.getText().substring(name_field.getCaretPosition(), name_field.getText().length());
					save_button.setEnabled(!modified.trim().isEmpty());
				}
				else if(e.getExtendedKeyCode() == KeyEvent.VK_SPACE){
					save_button.setEnabled(name_field.getText().trim().length() > 0);
				}
				else{
					save_button.setEnabled(true);
				}
			}
		});
		frame.setContentPane(content_panel);
		frame.pack();
		frame.setResizable(false);
		refreshSaves();
		refreshMaps();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void setSteamDir(File steam_dir) {
		Main.SETTINGS.setProperty("steam_path", steam_dir.getAbsolutePath());
	}

	public static File getSteamDir() {
		File file = new File(Main.SETTINGS.getProperty("steam_path"));
		if(file.exists()){
			return file;
		}
		return new File("/");
	}

	public static File[] listDirectories(File parent) {
		return parent.listFiles(Main.DIRECTORY_FILTER);
	}

	private void refreshMaps() {
		if (!checkSteamDir()) {
			return;
		}
		File[] servers = listDirectories(new File(getSteamDir() + "\\steamapps\\common\\Unturned\\Servers"));
		ArrayList<Server> server_objects = new ArrayList<>();
		for (File server : servers) {
			server_objects.add(new Server(server));
		}
		map_box.setModel(new MapTreeModel(server_objects.toArray(new Server[0])));
		save_button.setEnabled(false);
		name_field.setEnabled(false);
		server_data.setEnabled(false);
		player_data.setEnabled(false);
	}

	public static void saveMap(Server server, String map_name, String save_name, boolean include_player, boolean include_server) {
		if (!checkSteamDir()) {
			return;
		}
		ArrayList<ZipFileWrapper> files = new ArrayList<>();
		final String base_dir = getSteamDir() + "\\steamapps\\common\\Unturned\\Servers\\" + server.getName();
		File file = new File(base_dir + "\\Level\\" + map_name);
		files.add(new ZipFileWrapper(server.getMapFile(), file));
		if (include_player) {
			for (Player player : server.getPlayerData(map_name)) {
				files.add(new ZipFileWrapper(new File(server.getPlayerFile() + "\\" + player.getId() + "\\" + map_name), player.getFile()));
			}
		}
		if (include_server) {
			files.add(new ZipFileWrapper(server.getFile(), server.getServerFile()));
		}
		try {
			File save = new File(SAVE_DIR + "\\" + save_name + " (" + server.getName() + "#" + map_name + ").zip");
			save.createNewFile();
			new ZipProgress(save, files.toArray(new ZipFileWrapper[0]));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refreshSaves() {
		map_list.setModel(new MapListModel(SAVE_DIR.listFiles(Main.ZIP_FILTER)));
		load_button.setEnabled(false);
		delete_button.setEnabled(false);
		rename_button.setEnabled(false);
	}

	private static boolean checkSteamDir() {
		if (!getSteamDir().exists() && !getSteamDir().isDirectory() && !getSteamDir().getName().equals("Steam")) {
			new ConfigDialog();
			return false;
		}
		return true;
	}
}
