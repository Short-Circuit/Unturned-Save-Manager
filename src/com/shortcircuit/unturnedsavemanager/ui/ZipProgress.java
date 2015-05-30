package com.shortcircuit.unturnedsavemanager.ui;

import com.shortcircuit.unturnedsavemanager.zip.ZipCompressor;
import com.shortcircuit.unturnedsavemanager.zip.ZipExtractor;
import com.shortcircuit.unturnedsavemanager.zip.ZipFileWrapper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ZipProgress extends JDialog {
	private JPanel contentPane;
	private JProgressBar overall_progress;
	private JProgressBar file_progress;
	private JLabel file_label;
	private JLabel general_label;
	private boolean type;

	public ZipProgress(File dest, ZipFileWrapper[] sources) {
		this();
		type = false;
		general_label.setText("Compressing files...");
		int total_size = 0;
		for(ZipFileWrapper source : sources){
			total_size += ZipCompressor.folderSize(source.getFile());
		}
		overall_progress.setMaximum(total_size);
		new Thread(new ZipCompressor(this, dest, sources)).start();
	}

	private ZipProgress(){
		setContentPane(contentPane);
		setAlwaysOnTop(true);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});
		pack();
		setVisible(true);
	}

	public ZipProgress(File zip, File dest){
		this();
		type = true;
		general_label.setText("Extracting files...");
		new Thread(new ZipExtractor(this, zip, dest)).start();
	}

	public synchronized void updateOverall(int value) {
		overall_progress.setValue(value);
	}

	public synchronized void setCurrentFileData(String name, int size) {
		file_label.setText((type ? "Extracting" : "Compressing") + " file: " + name);
		file_progress.setMaximum(size);
	}

	public synchronized void updateFile(int value) {
		file_progress.setValue(value);
	}

	public void setOverallSize(int value){
		overall_progress.setMaximum(value);
	}
}
