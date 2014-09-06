package com.shortcircuit.unturnedsavemanager;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;

import com.shortcircuit.unturnedsavemanager.managers.FileWatchThread;
import com.shortcircuit.unturnedsavemanager.managers.SaveManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/*
 * @author ShortCircuit908
 */
public class UnturnedSaveManager {

    private JFrame frmUnturnedSaveManager;
    private JTextField name_text;
    public JList<String> save_list;
    private Thread search_thread;
    private JTextPane txt_save;
    private SaveManager save_manager;
    public JButton btn_select;
    public JButton btn_export;
    public JButton btn_confirm;
    public JButton btn_load;
    public JButton btn_delete;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UnturnedSaveManager window = new UnturnedSaveManager();
                    window.frmUnturnedSaveManager.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public UnturnedSaveManager() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        save_manager = new SaveManager();
        frmUnturnedSaveManager = new JFrame();
        frmUnturnedSaveManager.setResizable(false);
        frmUnturnedSaveManager.setTitle("Unturned Save Manager v1.0.1");
        frmUnturnedSaveManager.setIconImage(Toolkit.getDefaultToolkit().getImage(UnturnedSaveManager.class
                .getResource("/com/shortcircuit/unturnedsavemanager/resources/icon.png")));
        frmUnturnedSaveManager.setBounds(100, 100, 450, 300);
        frmUnturnedSaveManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmUnturnedSaveManager.getContentPane().setLayout(null);
        
        btn_select = new JButton("Select save");
        btn_select.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(save_list.getSelectedIndex() != -1) {
                    txt_save.setForeground(Color.BLACK);
                    txt_save.setText(save_list.getSelectedValue());
                    btn_load.setEnabled(true);
                    btn_delete.setEnabled(true);
                }
                else {
                    txt_save.setForeground(Color.LIGHT_GRAY);
                    txt_save.setText("Please select a save...");
                    btn_load.setEnabled(false);
                    btn_delete.setEnabled(false);
                }
            }
        });
        btn_select.setEnabled(false);
        btn_select.setBounds(10, 238, 201, 23);
        frmUnturnedSaveManager.getContentPane().add(btn_select);
        
        save_list = new JList<String>();
        save_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(save_list.getSelectedIndex() != -1) {
                    btn_select.setEnabled(true);
                }
                else {
                    btn_select.setEnabled(false);
                }
            }
        });
        save_list.setValueIsAdjusting(true);
        save_list.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        save_list.setBounds(10, 11, 201, 216);
        frmUnturnedSaveManager.getContentPane().add(save_list);

        btn_load = new JButton("Load");
        btn_load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(!txt_save.getText().equals("Please select a save...")) {
                    save_manager.importSave(txt_save.getText());
                }
            }
        });
        btn_load.setEnabled(false);
        btn_load.setBounds(232, 204, 96, 23);
        frmUnturnedSaveManager.getContentPane().add(btn_load);

        btn_delete = new JButton("Delete");
        btn_delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(!txt_save.getText().equals("Please select a save...")) {
                    File file = new File("Saves/" + txt_save.getText() + ".reg");
                    file.delete();
                }
            }
        });
        btn_delete.setEnabled(false);
        btn_delete.setBounds(338, 204, 96, 23);
        frmUnturnedSaveManager.getContentPane().add(btn_delete);

        btn_export = new JButton("Export current save");
        btn_export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                name_text.setText("");
                name_text.setEnabled(true);
                name_text.setEditable(true);
            }
        });
        btn_export.setBounds(233, 10, 201, 23);
        frmUnturnedSaveManager.getContentPane().add(btn_export);

        name_text = new JTextField();
        name_text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(name_text.getText().isEmpty()) {
                    name_text.setText("Please enter a name...");
                    name_text.setForeground(Color.LIGHT_GRAY);
                    name_text.setEditable(false);
                }
                else if(!name_text.getText().equals("Please enter a name...")){
                    name_text.setForeground(Color.BLACK);
                    name_text.setEditable(true);
                }
                btn_confirm.setEnabled(!name_text.getText().equals("Please enter a name..."));
            }
        });
        name_text.setForeground(Color.LIGHT_GRAY);
        name_text.setText("Please enter a name...");
        name_text.setEnabled(false);
        name_text.setEditable(false);
        name_text.setBounds(233, 44, 201, 23);
        frmUnturnedSaveManager.getContentPane().add(name_text);
        name_text.setColumns(10);

        txt_save = new JTextPane();
        txt_save.setForeground(Color.LIGHT_GRAY);
        txt_save.setText("Please select a save...");
        txt_save.setEditable(false);
        txt_save.setHighlighter(null);
        txt_save.setBounds(233, 238, 201, 23);
        frmUnturnedSaveManager.getContentPane().add(txt_save);

        btn_confirm = new JButton("Export");
        btn_confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(!name_text.getText().equals("Please enter a name...")){
                    save_manager.exportSave(name_text.getText());
                }
            }
        });
        btn_confirm.setEnabled(false);
        btn_confirm.setBounds(338, 78, 96, 23);
        frmUnturnedSaveManager.getContentPane().add(btn_confirm);
        
        search_thread = new Thread(new FileWatchThread(this));
        search_thread.start();
    }
}
