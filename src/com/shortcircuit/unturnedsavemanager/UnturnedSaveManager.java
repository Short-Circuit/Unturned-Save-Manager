package com.shortcircuit.unturnedsavemanager;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;

import com.shortcircuit.unturnedsavemanager.managers.FileWatchThread;
import com.shortcircuit.unturnedsavemanager.managers.RegistrySaveManager;
import com.shortcircuit.unturnedsavemanager.managers.SaveManager;

/**
 * @author ShortCircuit908
 */
public class UnturnedSaveManager extends JFrame{
    private static final long serialVersionUID = -1182579377443890522L;
    public final String version = "1.1.0";
    private JTextField name_text;
    public JList<String> save_list;
    private Thread search_thread;
    private JTextPane txt_save;
    private SaveManager save_manager;
    private RegistrySaveManager reg_manager;
    private JButton btn_select;
    private JButton btn_export;
    private JButton btn_confirm;
    private JButton btn_load;
    private JButton btn_delete;
    private JButton btn_reset;
    private JCheckBox export_players;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        new UnturnedSaveManager();
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
        reg_manager = new RegistrySaveManager();
        setResizable(false);
        setTitle("Unturned Save Manager v" + version);
        setIconImage(Toolkit.getDefaultToolkit().getImage(UnturnedSaveManager.class
                .getResource("/com/shortcircuit/unturnedsavemanager/resources/icon.png")));
        setBounds(100, 100, 450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        
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
        getContentPane().add(btn_select);
        
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
        getContentPane().add(save_list);

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
        getContentPane().add(btn_load);

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
        getContentPane().add(btn_delete);

        btn_export = new JButton("Export current map");
        btn_export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                name_text.setText("");
                name_text.setEnabled(true);
                name_text.setEditable(true);
            }
        });
        btn_export.setBounds(233, 44, 201, 23);
        getContentPane().add(btn_export);

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
        name_text.setBounds(233, 78, 201, 23);
        getContentPane().add(name_text);
        name_text.setColumns(10);

        txt_save = new JTextPane();
        txt_save.setForeground(Color.LIGHT_GRAY);
        txt_save.setText("Please select a save...");
        txt_save.setEditable(false);
        txt_save.setHighlighter(null);
        txt_save.setBounds(233, 238, 201, 23);
        getContentPane().add(txt_save);

        btn_confirm = new JButton("Export");
        btn_confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(!name_text.getText().equals("Please enter a name...")){
                    reg_manager.save(name_text.getText(), export_players.isSelected());
                }
            }
        });
        btn_confirm.setEnabled(false);
        btn_confirm.setBounds(338, 134, 96, 23);
        getContentPane().add(btn_confirm);
        
        btn_reset = new JButton("Reset current map");
        btn_reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                save_manager.resetSave();
            }
        });
        btn_reset.setBounds(233, 10, 201, 23);
        getContentPane().add(btn_reset);
        
        export_players = new JCheckBox("Include player data");
        export_players.setBounds(231, 108, 203, 23);
        getContentPane().add(export_players);
        
        search_thread = new Thread(new FileWatchThread(this));
        search_thread.start();
        setVisible(true);
    }
}
