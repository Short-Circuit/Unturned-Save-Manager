package com.shortcircuit.unturnedsavemanager.managers;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import com.shortcircuit.unturnedsavemanager.UnturnedSaveManager;

/**
 * @author ShortCircuit908
 * 
 */
public class FileWatchService implements Runnable{
    private WatchService watch_service;
    private UnturnedSaveManager application;
    private File directory = new File("Saves/");
    public FileWatchService(UnturnedSaveManager application) {
        this.application = application;
        directory.mkdirs();
        try {
            watch_service = FileSystems.getFileSystem(directory.toURI()).newWatchService();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    public void run() {
        WatchKey key = watch_service.poll();
        List<WatchEvent<?>> events = key.pollEvents();
        for(WatchEvent<?> event : events) {
            System.out.println(event.context());
        }
    }
}
