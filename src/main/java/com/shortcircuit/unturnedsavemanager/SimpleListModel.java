package com.shortcircuit.unturnedsavemanager;

import java.io.File;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * @author ShortCircuit908
 */
public class SimpleListModel implements ListModel<String> {
	private final File[] files;

	public SimpleListModel(File[] files) {
		this.files = files;
	}

	@Override
	public int getSize() {
		return files.length;
	}

	@Override
	public String getElementAt(int index) {
		return index >= 0 && index < files.length ? files[index].getName().replace(".zip", "") : null;
	}

	public File getFile(int index){
		return index >= 0 && index < files.length ? files[index] : null;
	}

	@Override
	public void addListDataListener(ListDataListener l) {

	}

	@Override
	public void removeListDataListener(ListDataListener l) {

	}
}
