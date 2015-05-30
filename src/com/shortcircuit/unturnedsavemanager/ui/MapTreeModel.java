package com.shortcircuit.unturnedsavemanager.ui;

import com.shortcircuit.unturnedsavemanager.structures.FileWrapper;
import com.shortcircuit.unturnedsavemanager.structures.Server;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author ShortCircuit908
 */
public class MapTreeModel implements TreeModel {
	private final Server[] servers;

	public MapTreeModel(Server... servers) {
		this.servers = servers;
	}

	@Override
	public String getRoot() {
		return "Servers";
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent.equals("Servers")){
			return servers[index];
		}
		if(parent instanceof Server){
			return ((Server) parent).getMaps()[index];
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent.equals("Servers")){
			return servers.length;
		}
		if(parent instanceof Server){
			return ((Server) parent).getMaps().length;
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof FileWrapper;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {

	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {

	}
}
