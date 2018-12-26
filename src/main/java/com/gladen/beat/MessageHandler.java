package com.gladen.beat;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;


public class MessageHandler extends ListenerAdapter {

    final String PF = Config.command_prefix.toLowerCase();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw().toLowerCase();
        if (!msg.startsWith(PF)) return;
        String cmd = msg.substring(PF.length());
        String[] args = new String[2];
        if (cmd.contains(" ")) {
            args = cmd.split(" ", 2);
            cmd = args[0];
        }
        YT yt = new YT();
        switch (cmd) {
            case "np":
                AudioTrack PlayingTrack = Login.audio.getPlayingTrack();
                e.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor("Now Playing", PlayingTrack.getInfo().uri, null)
                    .setColor(Color.RED)
                    .addField("Song Name", PlayingTrack.getInfo().title, true)
                    .addField("Channel", PlayingTrack.getInfo().author, true)
                    .addField("Song Progress", String.format("`%s / %s`", this.getLength(PlayingTrack.getPosition()), this.getLength(PlayingTrack.getInfo().length)), true)
                    .addField("Song Link", "[Youtube Link](" + PlayingTrack.getInfo().uri + ")", true)
                    .setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", PlayingTrack.getInfo().identifier))
                    .build()
            ).queue();
                break;
            case "stop":
                    e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Color.RED)
                    .appendDescription("Playback stopped")
                    .build()
                    ).queue();
                    Login.audio.stop();
                break;
            case "skip":
                    e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Color.RED)
                    .appendDescription("Skipping to next available track")
                    .build()
                    ).queue();
                    Login.audio.skip();
                break;
            case "play":
                if (args[1] == null || args[1].isEmpty()) {
                    Login.audio.play();
                    break;
                }
                String url = yt.query(args[1]);
                if (url.isEmpty()) {
                    //this won't happen unless youtube breaks
                    e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Error:")
                    .appendDescription("Could not find anything to play")
                    .build()
                    ).queue();
                } else Login.audio.play(url);
                break;
            case "volume":
                if (args[1] == null || args[1].isEmpty()) {
                    e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Current Serverwide Volume:")
                    .appendDescription(String.valueOf(Login.audio.getVolume()))
                    .build()
                    ).queue();
                    break;
                }
                int newVol = Integer.parseInt(args[1]);
                Login.audio.setVolume(newVol);
                break;
            default:
                break;
        }
    }

    private String getLength(long length) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
        );
    }
}
