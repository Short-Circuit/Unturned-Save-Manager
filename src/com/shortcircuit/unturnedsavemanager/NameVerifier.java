package com.shortcircuit.unturnedsavemanager;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * @author ShortCircuit908
 * 
 */
public class NameVerifier extends InputVerifier{
    private JButton button_confirm;
    private JCheckBox check_box;
    public NameVerifier(JButton button_confirm, JCheckBox check_box) {
        this.button_confirm = button_confirm;
        this.check_box = check_box;
    }
    @Override
    public boolean verify(JComponent component) {
        // TODO Auto-generated method stub
        JTextField label = (JTextField)component;
        boolean verified = !label.getText().equals("Please enter a name...")
                && !label.getText().trim().isEmpty();
        button_confirm.setEnabled(verified);
        label.setEnabled(verified);
        check_box.setEnabled(verified);
        if(!verified) {
            label.setText("Please enter a name...");
        }
        return verified;
    }
}
