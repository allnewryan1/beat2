package com.gladen.beat;

import java.io.BufferedWriter;

/*
 * COPYRIGHT Gladen Software 2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

 /**
  * Simple preference class, reads and writes key=value style with no character escaping.
  * foolishly assumes user knows what he or she is doing
  */
  public class DummyPrefs {

    public static DummyPrefs instance;

    public static DummyPrefs getPrefs() {
        if (instance == null) instance = new DummyPrefs();
        return instance;
    }


    File cFile = new File("config");

    private DummyPrefs() {
        try (Scanner scan = new Scanner(cFile)) {
            if (!scan.hasNextLine()) {
            JOptionPane.showMessageDialog(null, "Config file empty");
            throw new FileNotFoundException();
            }
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                System.err.println(line);
                String[] pref = line.split("=");
                if (pref[0].equals("discord_token")) Config.discord_token = pref[1];
                if (pref[0].equals("yt_api_key")) Config.yt_api_key = pref[1];
                if (pref[0].equals("command_prefix")) Config.command_prefix = pref[1];
                if (pref[0].equals("volume")) Config.volume = Integer.parseInt(pref[1]);
                if (pref[0].equals("text_channel_out")) Config.text_channel_out = pref[1];
                if (pref[0].equals("voice_channel_out")) Config.voice_channel_out = pref[1];
            }
        } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Config file not found");

        }
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::savePrefs, 2, 2, TimeUnit.HOURS);
    }

    public void savePrefs() {
        try {
            if (cFile.exists())
                if(!cFile.delete()) throw new IOException();
            if (!cFile.createNewFile()) throw new IOException();
            BufferedWriter writer = new BufferedWriter(new FileWriter(cFile));
            writer.write("discord_token" + "=" + Config.discord_token);
            writer.newLine();
            writer.write("yt_api_key" + "=" + Config.yt_api_key);
            writer.newLine();
            writer.write("command_prefix" + "=" + Config.command_prefix);
            writer.newLine();
            writer.write("volume" + "=" + Config.volume);
            writer.newLine();
            writer.write("text_channel_out" + "=" + Config.text_channel_out);
            writer.newLine();
            writer.write("voice_channel_out" + "=" + Config.voice_channel_out);
            writer.newLine();
            writer.close();
            } catch (IOException e) {
                if (build.type == build.TYPE.DEBUG) Main.out.println(e.getMessage());
            }
    }
}