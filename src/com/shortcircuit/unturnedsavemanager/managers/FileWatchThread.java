/**
 * @author ShortCircuit908
 */
package com.shortcircuit.unturnedsavemanager.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import org.apache.commons.lang3.ArrayUtils;

import com.shortcircuit.unturnedsavemanager.UnturnedSaveManager;

/**
 * @author ShortCircuit908
 *
 */
public class FileWatchThread implements Runnable{
    private UnturnedSaveManager application;
    private File directory = new File("Saves/");
    private File[] previousFiles = new File[] {};
    public FileWatchThread(UnturnedSaveManager application){
        this.application = application;
        directory.mkdirs();
    }
    @Override
    public void run(){
        while(true){
            removeFiles();
            boolean foundFile = false;
            for(File file : directory.listFiles()){
                if(!ArrayUtils.contains(previousFiles, file)){
                    foundFile = true;
                    String[] vals = new String[]{};
                    for(int i = 0; i < application.save_list.getModel().getSize(); i++){
                        vals = ArrayUtils.add(vals, application.save_list.getModel().getElementAt(i));
                    }
                    vals = ArrayUtils.add(vals, file.getName().replace(".reg", ""));
                    final String[] newVals = vals;
                    application.save_list.setModel(new AbstractListModel<String>() {
                        private static final long serialVersionUID = -8780553823693181883L;
                        String[] values = newVals;
                        public int getSize() {
                            return values.length;
                        }
                        public String getElementAt(int index) {
                            return values[index];
                        }
                    });
                }
            }
            if(foundFile) {
                previousFiles = directory.listFiles();
            }
            try{
                Thread.sleep(5000);
            }
            catch(InterruptedException e){
                
            }
        }
    }
    public void removeFiles(){
        List<String> valsToRemove = new ArrayList<String>();
        for(int i = 0; i < application.save_list.getModel().getSize(); i++){
            File file = new File("Saves/" + application.save_list.getModel().getElementAt(i) + ".reg");
            if(!file.exists()){
                valsToRemove.add(application.save_list.getModel().getElementAt(i));
            }
        }
        for(String val : valsToRemove){
            String[] vals = new String[]{};
            for(int i = 0; i < application.save_list.getModel().getSize(); i++){
                vals = ArrayUtils.add(vals, application.save_list.getModel().getElementAt(i));
            }
            vals = ArrayUtils.removeElement(vals, val);
            final String[] newVals = vals;
            application.save_list.setModel(new AbstractListModel<String>() {
                private static final long serialVersionUID = -4307935392507444592L;
                String[] values = newVals;
                public int getSize() {
                    return values.length;
                }
                public String getElementAt(int index) {
                    return values[index];
                }
            });
        }
    }
}
