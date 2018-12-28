package com.gladen.beat;

/**
 * COPYRIGHT Gladen Software 2018
 */

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

//TODO add license file, uses lavaplayer under Apache 2.0 license

public class Login {
    static JDA Jda;
    static Controller audio;

    public static void main(String args[]) {
        if (!Config.DEBUG) {
            //Open message
            String open = "=============================================================\n" +
                          "|  0000   0000   000  0000000         0000    000  0000000  |\n" +
                          "|  0   0  0     0   0    0            0   0  0   0    0     |\n" +
                          "|  0000   000   00000    0    =====   0000   0   0    0     |\n" +
                          "|  0   0  0     0   0    0            0   0  0   0    0     |\n" +
                          "|  0000   0000  0   0    0            0000    000     0     |\n" +
                          "=============================================================";
            System.out.println(open);
        }
        try {
            if (isNas()) {
                System.err.println("Native audio support is enabled");
                Jda = new JDABuilder(AccountType.BOT)
                        .setToken(Config.discord_token)
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .buildBlocking();
            } else {
                Jda = new JDABuilder(AccountType.BOT)
                        .setToken(Config.discord_token)
                        .buildBlocking();
            }
            Jda.addEventListener(new MessageHandler());
            System.out.println("\n\n..........................................................................................................");
            System.out.println("Use this url to add me:\n" + "https://discordapp.com/oauth2/authorize?client_id=" + Jda.getSelfUser().getId() + "&scope=bot&permissions=37215296");
            System.out.println("..........................................................................................................\n\n");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        audio = new Controller();
    }

    private static boolean isNas() {
        String os = System.getProperty("os.name");
        return (os.contains("Windows") || os.contains("Linux"))
                && !System.getProperty("os.arch").equalsIgnoreCase("arm")
                && !System.getProperty("os.arch").equalsIgnoreCase("arm-linux");

    }
}
