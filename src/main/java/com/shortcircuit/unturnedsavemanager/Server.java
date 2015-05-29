package com.shortcircuit.unturnedsavemanager;

import java.io.File;
import java.util.ArrayList;

/**
 * @author ShortCircuit908
 */
public class Server {
	private final File server;
	private final File map_file;
	private final File player_file;
	private final File server_file;
	private final String[] maps;

	public Server(File server) {
		this.server = server;
		this.map_file = new File(server + "\\Level");
		this.player_file = new File(server + "\\Players");
		this.server_file = new File(server + "\\Server");
		File[] map_files = map_file.listFiles(Main.DIRECTORY_FILTER);
		maps = new String[map_files.length];
		for (int i = 0; i < map_files.length; i++) {
			maps[i] = map_files[i].getName();
		}
	}

	public String getName(){
		return server.getName();
	}

	public File getFile(){
		return server;
	}

	public File getMapFile(){
		return map_file;
	}

	public File getPlayerFile(){
		return player_file;
	}

	public File getServerFile(){
		return server_file;
	}

	public Player[] getPlayerData(String map_name){
		ArrayList<Player> players = new ArrayList<>();
		for(File player_data : player_file.listFiles(Main.DIRECTORY_FILTER)) {
			players.add(new Player(player_data, map_name));
		}
		return players.toArray(new Player[0]);
	}

	public FileWrapper[] getMaps(){
		File[] files = map_file.listFiles(Main.DIRECTORY_FILTER);
		FileWrapper[] wrappers = new FileWrapper[files.length];
		for(int i = 0; i < files.length; i++){
			wrappers[i] = new FileWrapper(files[i]);
		}
		return wrappers;
	}

	@Override
	public String toString(){
		return getName();
	}
}
