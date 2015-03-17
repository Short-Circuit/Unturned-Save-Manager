package com.shortcircuit.unturnedsavemanager.swing;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Confirmation extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel message_label;
	private final Method invoke_on_OK;
	private final Method invoke_on_cancel;
	private final Object parent;
	private Object[] method_args;

	public Confirmation(Object parent, Point point, Image icon_image, String title, String message, Method invoke_on_OK, Method invoke_on_cancel, Object... method_args) {
		this.parent = parent;
		this.invoke_on_OK = invoke_on_OK;
		this.invoke_on_cancel = invoke_on_cancel;
		this.method_args = method_args;
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		setTitle(title);
		setLocation(point);
		setIconImage(icon_image);
		message_label.setText(message);
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

// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		pack();
		setVisible(true);
	}

	private void onOK() {
		if(invoke_on_OK != null) {
			try {
				invoke_on_OK.setAccessible(true);
				invoke_on_OK.invoke(parent, method_args);
			}
			catch(ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		dispose();
	}

	private void onCancel() {
		if(invoke_on_cancel != null) {
			try {
				invoke_on_cancel.setAccessible(true);
				invoke_on_cancel.invoke(parent, method_args);
			}
			catch(ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		dispose();
	}
}
