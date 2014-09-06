package com.shortcircuit.unturnedsavemanager.managers;

import java.io.File;
import java.io.IOException;

/* 
 * @author ShortCircuit908
 */
public class SaveManager {
    public SaveManager() {

    }
    public void exportSave(String name) {
        try {
            String path = new File("Saves/" + name + ".reg").getAbsolutePath();
            Runtime.getRuntime().exec("REG EXPORT \"HKEY_CURRENT_USER\\Software\\Smartly Dressed Games\\"
                    + "Unturned\" \"" + path + "\" /y");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    public boolean importSave(String name) {
        File save = new File("Saves/" + name + ".reg");
        if(save.exists()) {
            String path = save.getAbsolutePath();
            try {
                Runtime.getRuntime().exec("REG IMPORT \"" + path + "\"");
                return true;
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
