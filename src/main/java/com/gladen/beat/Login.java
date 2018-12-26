package com.gladen.beat;

import com.kaaz.configuration.ConfigurationBuilder;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.File;

public class Login {
    static JDA Jda;
    static Controller audio;

    public static void main(String args[]) {
        try {
            new ConfigurationBuilder(Config.class, new File("bot.cfg")).build(true);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        try {
            if (isNas()) {
                System.out.println("Enabling native audio sending");
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
            System.out.println("Use this url to add me:\n" + "https://discordapp.com/oauth2/authorize?client_id=" + Jda.getSelfUser().getId() + "&scope=bot&permissions=37215296");
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
