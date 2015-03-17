package com.shortcircuit.unturnedsavemanager.swing;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * @author ShortCircuit908
 */
public class NameVerifier extends InputVerifier {
	private JButton button_confirm;

	public NameVerifier(JButton button_confirm) {
		this.button_confirm = button_confirm;
	}

	@Override
	public boolean verify(JComponent component) {
		if(component instanceof JTextComponent) {
			JTextComponent label = (JTextComponent)component;
			boolean verified = !label.getText().trim().isEmpty();
			button_confirm.setEnabled(verified);
			return verified;
		}
		return false;
	}
}
