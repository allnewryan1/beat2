package com.gladen.beat;

/*
 * COPYRIGHT Gladen Software 2020
 */

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

//TODO add license file, uses lavaplayer under Apache 2.0 license

public class Login {
    static JDA Jda;
    static Controller audio;
    public static List<String> textChannels = new ArrayList<>();
    public static List<String> voiceChannels = new ArrayList<>();
    public static String[] textChannels1;
    public static String[] voiceChannels1;

    public static void main(String[] args) {
        if (build.type == build.TYPE.DEBUG) {
            /*
            //Open message
            String open = "============================================================\n" +
                          "|  0000   0000   000  0000000        0000    000  0000000  |\n" +
                          "|  0   0  0     0   0    0           0   0  0   0    0     |\n" +
                          "|  0000   000   00000    0   =====   0000   0   0    0     |\n" +
                          "|  0   0  0     0   0    0           0   0  0   0    0     |\n" +
                          "|  0000   0000  0   0    0           0000    000     0     |\n" +
                          "============================================================";
                          */
            String op =   "================================================\n" +
                          "|  0000    0000    000    000000              0000     000    000000   |\n" +
                          "|  0       0  0         0      0       0                     0      0  0     0        0         |\n" +
                          "|  0000    000    00000       0     =====    0000   0      0       0          |\n" +
                          "|  0       0  0         0      0       0                     0      0  0     0        0         |\n" +
                          "|  0000    0000  0      0       0                     0000     000         0          |\n" +
                          "================================================";

            Main.out.println(op);
        }
        try {
            if (isNas()) {
                Debug.log("Native audio support is enabled");
                Jda = JDABuilder.createDefault(Config.discord_token)
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .build();
            } else {
                Jda = JDABuilder.createDefault(Config.discord_token)
                        .build();
            }
            Jda.awaitReady();
            Jda.addEventListener(new MessageHandler());
            Main.out.println("\n\n..........................................................................................................");
            Main.out.println("Use this url to add me:\n" + "https://discordapp.com/oauth2/authorize?client_id=" + Jda.getSelfUser().getId() + "&scope=bot&permissions=37215296");
            Main.out.println("..........................................................................................................\n\n");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        audio = new Controller();
        SnowflakeCacheView<TextChannel> textTemp = Jda.getTextChannelCache();
        SnowflakeCacheView<VoiceChannel> voiceTemp = Jda.getVoiceChannelCache();
        for (TextChannel c : textTemp) {
            textChannels.add(c.getName());
        }
        for (VoiceChannel v : voiceTemp) {
            voiceChannels.add(v.getName());
        }
        textChannels1 = new String[textChannels.size()];
        for (int i = 0; i < textChannels.size(); i++) {
            textChannels1[i] = textChannels.get(i);
        }

        voiceChannels1 = new String[voiceChannels.size()];
        for (int i = 0; i < voiceChannels.size(); i++) {
            voiceChannels1[i] = voiceChannels.get(i);
        }
    }

    private static boolean isNas() {
        String os = System.getProperty("os.name");
        Debug.log(os);
        return (os.contains("Windows") || os.contains("Linux"))
                && !System.getProperty("os.arch").equalsIgnoreCase("arm")
                && !System.getProperty("os.arch").equalsIgnoreCase("arm-linux");

    }
}
