package com.shortcircuit.unturnedsavemanager.ui;

import com.shortcircuit.unturnedsavemanager.Main;
import com.shortcircuit.unturnedsavemanager.structures.ReturnCallback;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

/**
 * @author ShortCircuit908
 */
public abstract class JInputDialog<T> extends JDialog {
	protected ReturnCallback<T> callback;

	public JInputDialog(ReturnCallback<T> callback) {
		this.callback = callback;
		try {
			setIconImage(ImageIO.read(Main.class.getResourceAsStream("resources/icon.png")));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doReturn(T value) {
		callback.setValue(value);
		dispose();
	}
}
