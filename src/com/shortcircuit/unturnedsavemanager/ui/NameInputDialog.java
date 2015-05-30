package com.shortcircuit.unturnedsavemanager.ui;

import com.shortcircuit.unturnedsavemanager.structures.ReturnCallback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class NameInputDialog extends JInputDialog<String> {
	private JPanel content_pane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField input_field;
	private JLabel message_label;
	private boolean input_ok = false;

	public NameInputDialog(String message, ReturnCallback<String> callback) {
		super(callback);
		setContentPane(content_pane);
		setModal(true);
		setLocationByPlatform(true);
		getRootPane().setDefaultButton(buttonOK);
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

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		content_pane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		input_field.setVerifyInputWhenFocusTarget(true);
		input_field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				checkInput(e.getExtendedKeyCode());
			}
		});
		pack();
		setVisible(true);
	}

	private void onOK() {
		checkInput(KeyEvent.VK_SPACE);
		System.out.println(input_ok);
		if(input_ok) {
			doReturn(input_field.getText().trim());
		}
	}

	private void onCancel() {
		doReturn(null);
	}

	private void checkInput(int keycode){
		if(keycode == KeyEvent.VK_BACK_SPACE){
			String modified = input_field.getText().trim().isEmpty() ? "" : input_field.getText().substring(0, input_field.getCaretPosition() - 1) + input_field.getText().substring(input_field.getCaretPosition(), input_field.getText().length());
			input_ok = !modified.trim().isEmpty();
		}
		else if(keycode == KeyEvent.VK_SPACE){
			input_ok = input_field.getText().trim().length() > 0;
		}
		else{
			input_ok = true;
		}
		buttonOK.setEnabled(input_ok);
	}
}
