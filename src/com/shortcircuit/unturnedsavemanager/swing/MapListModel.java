package com.shortcircuit.unturnedsavemanager.swing;

import com.shortcircuit.unturnedsavemanager.objects.MapSave;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author ShortCircuit908
 */
public class MapListModel implements ListModel<MapSave>, Runnable {
	private final File save_dir = new File("Saves");
	private final List<MapSave> saves = new ArrayList<>();
	private final Set<ListDataListener> listeners = new HashSet<>();
	private boolean halted = false;
	private WatchKey watch_key;

	public MapListModel() {
		save_dir.mkdirs();
		for(File file : save_dir.listFiles()) {
			saves.add(new MapSave(file));
		}
		try {
			Path path = save_dir.toPath();
			watch_key = path.register(FileSystems.getDefault().newWatchService(), StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSize() {
		return saves.size();
	}

	@Override
	public MapSave getElementAt(int index) {
		return saves.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	@Override
	public void run() {
		while(!halted) {
			List<WatchEvent<?>> events = watch_key.pollEvents();
			if(!events.isEmpty()) {
				saves.clear();
				for(File file : save_dir.listFiles()) {
					saves.add(new MapSave(file));
				}
				for(ListDataListener listener : listeners) {
					listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, saves.size()));
				}
			}
		}
	}

	public void halt() {
		halted = true;
	}
}
