package com.gladen.beat;

import java.io.BufferedWriter;

/**
 * COPYRIGHT Gladen Software 2018
 *
 * Simple preference class, reads and writes key=value style with no character escaping.
 * foolishly assumes user knows what he or she is doing
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class DummyPrefs {
    File cFile = new File("config");
    public HashMap<String, String> prefs = new HashMap<>();
    int prefsHash = 0;

    public DummyPrefs() {
        try (Scanner scan = new Scanner(cFile)) {
            if (!scan.hasNextLine()) {
            JOptionPane.showMessageDialog(null, "Config file empty");
            }
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                System.err.println(line);
                String[] pref = line.split("=");
                if (pref[0].equals("debug")) Config.DEBUG = Boolean.valueOf(pref[1]);
                if (pref[0].equals("discord_token")) Config.discord_token = pref[1];
                if (pref[0].equals("command_prefix")) Config.command_prefix = pref[1];
                if (pref[0].equals("volume")) Config.volume = Integer.parseInt(pref[1]);
                if (pref[0].equals("yt_api_key")) Config.yt_api_key = pref[1];
                prefs.put(pref[0], pref[1]);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Config file not found");
        }
        prefsHash = prefs.hashCode();
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                savePrefs();
            }
        }, 2, 2, TimeUnit.HOURS);
    }

    public void savePrefs() {
        //if (prefsHash == prefs.hashCode()) return; //not how this works...will be different every instance
        try {
            if (cFile.exists()) cFile.delete();
            cFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(cFile));
            for(Map.Entry<String, String> entry : prefs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                writer.write(key + "=" + value);
                writer.newLine();
                }
            writer.close();
            } catch (IOException e) {

            }
    }
}