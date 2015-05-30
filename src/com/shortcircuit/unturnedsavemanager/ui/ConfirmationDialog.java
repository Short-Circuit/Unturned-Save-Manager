package com.shortcircuit.unturnedsavemanager.ui;

import com.shortcircuit.unturnedsavemanager.structures.ReturnCallback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ConfirmationDialog extends JInputDialog<Boolean> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel message_label;

	public ConfirmationDialog(String message, ReturnCallback<Boolean> callback) {
		super(callback);
		setModal(true);
		setLocationByPlatform(true);
		setContentPane(contentPane);
		getRootPane().setDefaultButton(buttonOK);
		message_label.setText(message);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		pack();
		setVisible(true);
	}

	private void onOK() {
		doReturn(true);
	}

	private void onCancel() {
		doReturn(false);
	}

}
