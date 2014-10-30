package com.shortcircuit.unturnedsavemanager.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;


/**
 * @author ShortCircuit908
 * 
 */
public class RegistrySaveManager {
    public RegistrySaveManager() {

    }
    public void save(String name, boolean save_players) {
        try{
            String dir = "\"HKEY_CURRENT_USER\\Software\\Smartly Dressed Games\\Unturned\"";
            String data = "Windows Registry Editor Version 5.00\n\n[HKEY_CURRENT_USER\\Software\\Smartly Dressed Games\\Unturned]\n";
            Process proc = Runtime.getRuntime().exec("REG QUERY " + dir);
            Scanner scan = new Scanner(proc.getInputStream());
            while(scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if(line.startsWith("barricades") || line.startsWith("structures")
                        || ((line.startsWith("inventory") || line.startsWith("clothes")
                                || line.startsWith("last") || line.startsWith("skills")
                                || line.startsWith("life") || line.startsWith("position")) && save_players)){
                    if(!line.equals(dir.replace("\"", "")) && !line.isEmpty()) {
                        String[] values = line.split("    ");
                        String parsed = "\"" + values[0] + "\"=";
                        switch(values[1]) {
                        case "REG_SZ":
                            if(values.length < 3) {
                                parsed += "\"\"";
                                break;
                            }
                            parsed += "\"" + values[2] + "\"";
                            break;
                        case "REG_DWORD":
                            parsed += values[2];
                            break;
                        }
                        data += parsed + "\n";
                    }
                }
            }
            scan.close();
            System.out.println(data);
            composeReg(name, data);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void composeReg(String name, String data) {
        try {
            File save = new File("Saves/" + name + ".reg");
            save.createNewFile();
            FileOutputStream stream = new FileOutputStream(save);
            stream.write(data.getBytes());
            stream.flush();
            stream.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
