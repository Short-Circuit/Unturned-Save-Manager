package com.shortcircuit.unturnedsavemanager.zip;

import com.shortcircuit.unturnedsavemanager.ui.ZipProgress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ShortCircuit908
 */
public class ZipExtractor implements Runnable {
	private final File zip;
	private final File dest;
	private final ZipProgress parent;

	public ZipExtractor(ZipProgress parent, File zip, File dest) {
		this.parent = parent;
		this.zip = zip;
		this.dest = dest;
	}

	@Override
	public void run() {
		extract(zip, dest);
	}

	private void extract(File zip, File dest) {
		byte[] buffer = new byte[1];
		try {
			ZipFile zip_file = new ZipFile(zip);
			int total_size = 0;
			Enumeration<? extends ZipEntry> entries = zip_file.entries();
			while(entries.hasMoreElements()){
				total_size += entries.nextElement().getSize();
			}
			parent.setOverallSize(total_size);
			entries = zip_file.entries();
			int total_written = 0;
			while(entries.hasMoreElements()){
				ZipEntry entry = entries.nextElement();
				int entry_size = (int)entry.getSize();
				parent.setCurrentFileData(entry.getName(), entry_size);
				String file_name = entry.getName();
				File file = new File(dest + "\\" + file_name);
				new File(file.getParent()).mkdirs();
				FileOutputStream out = new FileOutputStream(file);
				int len;
				int written = 0;
				InputStream in = zip_file.getInputStream(entry);
				while ((len = in.read(buffer)) != -1) {
					written++;
					total_written++;
					parent.updateOverall(total_written);
					parent.updateFile(written);
					out.write(buffer, 0, len);
				}
				out.flush();
				out.close();
				in.close();
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		parent.dispose();
	}
}
