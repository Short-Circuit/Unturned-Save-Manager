package com.shortcircuit.unturnedsavemanager.swing;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class InputConfirmation extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField name_field;
	private JCheckBox save_players;
	private final Object parent;
	private final Method invoke_on_OK;

	public InputConfirmation(Object parent, Point point, Image icon_image, String title, Method invoke_on_OK) {
		this.parent = parent;
		this.invoke_on_OK = invoke_on_OK;
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		setLocation(point);
		setIconImage(icon_image);
		setTitle(title);

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
		final NameVerifier verifier = new NameVerifier(buttonOK);
		name_field.setInputVerifier(verifier);
		name_field.setVerifyInputWhenFocusTarget(true);

		name_field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				verifier.verify(name_field);
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
				invoke_on_OK.invoke(parent, name_field.getText(), save_players.isSelected());
			}
			catch(ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		dispose();
	}

	private void onCancel() {
		dispose();
	}
}
