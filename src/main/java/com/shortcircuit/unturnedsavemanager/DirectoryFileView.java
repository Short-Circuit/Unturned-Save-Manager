package com.shortcircuit.unturnedsavemanager;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

/**
 * @author ShortCircuit908
 */
public class DirectoryFileView extends FileView {
	@Override
	public Icon getIcon(File file){
		return FileSystemView.getFileSystemView().getSystemIcon(file);
	}
}
