package com.shortcircuit.unturnedsavemanager.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ConfigDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;

	public ConfigDialog() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		setLocationByPlatform(true);

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		pack();
		setVisible(true);
	}

	private void onOK() {
		dispose();
	}
}
