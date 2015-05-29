package com.shortcircuit.unturnedsavemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor implements Runnable {
	private int total_read = 0;
	private final File output;
	private final ZipFileWrapper[] sources;
	private final ZipProgress parent;

	public ZipCompressor(ZipProgress parent, File output, ZipFileWrapper[] sources) {
		this.parent = parent;
		this.output = output;
		this.sources = sources;
	}

	public void run() {
		try {
			packZip(output, sources);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void packZip(File output, ZipFileWrapper[] sources) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(output));
		zipOut.setLevel(Deflater.BEST_COMPRESSION);
		for (ZipFileWrapper source : sources) {
			if (source.getFile().isDirectory()) {
				zipDir(zipOut, "", source);
			}
			else {
				zipFile(zipOut, "", source.getFile());
			}
		}
		zipOut.flush();
		zipOut.close();
		parent.dispose();
	}

	private String buildPath(String path, String file) {
		if (path == null || path.isEmpty()) {
			return file;
		}
		return path + "/" + file;
	}

	private void zipDir(ZipOutputStream zos, String path, ZipFileWrapper dir) throws IOException {
		if (!dir.getFile().canRead()) {
			return;
		}
		File[] files = dir.getFile().listFiles();
		path = buildPath(path, dir.getZipDir());
		for (File source : files) {
			if (source.isDirectory()) {
				zipDir(zos, path, source);
			}
			else {
				zipFile(zos, path, source);
			}
		}
	}

	private void zipDir(ZipOutputStream zos, String path, File dir) throws IOException {
		if (!dir.canRead()) {
			return;
		}
		File[] files = dir.listFiles();
		path = buildPath(path, dir.getName());
		for (File source : files) {
			if (source.isDirectory()) {
				zipDir(zos, path, source);
			}
			else {
				zipFile(zos, path, source);
			}
		}
	}

	private void zipFile(ZipOutputStream zos, String path, File file) throws IOException {
		if (!file.canRead()) {
			return;
		}
		zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[1];
		int byteCount;
		final int size = fis.available();
		parent.setCurrentFileData(file.getName(), size);
		int bytes_read = 0;
		while ((byteCount = fis.read(buffer)) != -1) {
			bytes_read++;
			parent.updateFile(bytes_read);
			total_read++;
			parent.updateOverall(total_read);
			zos.write(buffer, 0, byteCount);
		}

		fis.close();
		zos.closeEntry();
	}

	public static int folderSize(File directory) {
		int length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				length += file.length();
			}
			else {
				length += folderSize(file);
			}
		}
		return length;
	}
}