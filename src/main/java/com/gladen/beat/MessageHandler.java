package com.gladen.beat;

/*
 * COPYRIGHT Gladen Software 2020
 */

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MessageHandler extends ListenerAdapter {

    final String PF = Config.command_prefix.toLowerCase();

    long lastMessageRecieved = 0;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if(e.getMember() == null || e.getMember().getVoiceState() == null || e.getAuthor().isBot()) return;
        String msg = e.getMessage().getContentRaw().toLowerCase();
        if (!msg.startsWith(PF)) return;
        VoiceChannel userChannel = e.getMember().getVoiceState().getChannel();
        if (userChannel == null) {
            e.getChannel().sendMessage("You should probably be connected to a voice channel before using me :)").queue();
            return;
        } else {
            Login.audio.desiredChannel = userChannel.getId();
        }
        if (System.currentTimeMillis() - lastMessageRecieved <= 2000) {
            e.getChannel().sendMessage("Slow down there pal!  (Command not executed)").queue();
            return;
        }
        lastMessageRecieved = System.currentTimeMillis();
        String cmd = msg.substring(PF.length());
        String[] args = new String[8];
        if (cmd.contains(" ")) {
            if (cmd.startsWith("play") || cmd.startsWith("add")) args = cmd.split(" ", 2);
            else args = cmd.split(" ");
            cmd = args[0];
        }
        TextChannel c = e.getChannel();
        AudioLoadResultCallback callback = new AudioLoadResultCallback(){
            @Override
            public void trackLoaded(AudioTrack track) {
                c.sendMessage(new EmbedBuilder()
                    .setAuthor("Now Playing", track.getInfo().uri, null)
                    .setColor(Color.RED)
                    .addField("Song Name", track.getInfo().title, true)
                    .addField("Channel", track.getInfo().author, true)
                    .addField("Length", String.format("`%s / %s`", getLength(track.getPosition()), getLength(track.getInfo().length)), true)
                    .addField("Song Link", "[Youtube Link](" + track.getInfo().uri + ")", true)
                    .setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", track.getInfo().identifier))
                    .build()
            ).queue();
            Login.audio.unregisterEventListener(this);
            }
            @Override
            public void loadFailed(FriendlyException ex) {
                c.sendMessage("Failed to load track - try different keywords?").queue();
                ex.printStackTrace();
            Login.audio.unregisterEventListener(this);
            }
        };
        Login.audio.registerEventListener(callback);
        YT yt = new YT();
        switch (cmd) {
            case "help":
                if (args[1] == null || args[1].isEmpty()) {
                c.sendMessage(new EmbedBuilder()
                    .setTitle("Available Commands")
                    .addField("'play', 'play (search string)''", "With no args, plays the current queue. With (search string) plays the first result returned from YouTube", false)
                    .addField("pause", "pauses playback, use 'play' to resume", false)
                    .addField("add", "adds a song to the queue but does not immediately begin playback", false)
                    .addField("stop", "Stops playback, does not currently reset the queue (next 'play' command will start next song in queue)", false)
                    .addField("'nowplaying', 'now', 'np'", "Displays the current song info", false)
                    .addField("skip","Skips to the next song in the queue", false)
                    .addField("volume (number 1-100)", "Sets server playback volume to specified value. Note this does not affect any users individual volume setting on the bot itself", false)
                    .addField("queue", "Queue manipulation. For more details use '" + Config.command_prefix + "help queue'", false)
                .build()).queue();
                    break;
                }
                if (args[1].equals("queue")) {
                    c.sendMessage(new EmbedBuilder()
                        .setTitle("Queue Commands")
                        .addField("list", "list songs currently in queue", false)
                        .addField("clear", "clears the queue", false)
                        .addField("remove", "remove song at given position", false)
                    .build()).queue();
                }
                break;
            case "np":
            case "now":
            case "nowplaying":
                AudioTrack PlayingTrack = Login.audio.getPlayingTrack();
                if (PlayingTrack == null) {
                    c.sendMessage("Nothing currently playing.").queue();
                    break;
                }
                c.sendMessage(new EmbedBuilder()
                    .setAuthor("Now Playing")
                    .setColor(Color.RED)
                    .addField("Song Name", PlayingTrack.getInfo().title, true)
                    .addField("Channel", PlayingTrack.getInfo().author, true)
                    .addField("Length", String.format("`%s / %s`", this.getLength(PlayingTrack.getPosition()), this.getLength(PlayingTrack.getInfo().length)), true)
                    .addField("Song Link", "[Youtube Link](" + PlayingTrack.getInfo().uri + ")", true)
                    .setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", PlayingTrack.getInfo().identifier))
                    .build()).queue();
                break;
            case "pause":
                c.sendMessage("Playback paused").queue();
                Login.audio.pause();
                break;
            case "stop":
                c.sendMessage("Playback stopped").queue();
                Login.audio.stop();
                break;
            case "skip":
                c.sendMessage("Skipping to next available track").queue();
                Login.audio.skip();
                Login.audio.q.onModify();
                break;
            case "add":
                if (args[1] == null || args[1].isEmpty()) {
                    c.sendMessage("").queue();
                    break;
                }
                String addUrl = yt.query(args[1]);
                Login.audio.q.add(addUrl);
                String title = yt.getTrackInfo(addUrl).title;
                c.sendMessage("Added \"" + title + "\" to the queue").queue();
                break;
            case "play":
                if (args[1] == null || args[1].isEmpty()) {
                    if (Login.audio.isPaused()) c.sendMessage("Resuming playback").queue();
                    Login.audio.play();
                    break;
                }
                String url = yt.query(args[1]);
                if (url.isEmpty()) {
                    //this won't happen unless youtube breaks
                    c.sendMessage(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Error:")
                    .appendDescription("Could not find anything to play")
                    .build()
                    ).queue();
                } else Login.audio.play(url);
                break;
            case "volume":
                if (args[1] == null || args[1].isEmpty()) {
                    c.sendMessage(new EmbedBuilder()
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
            case "queue": //TODO queue manipulation (shuffle)
                if (args[1] == null || args[1].isEmpty()) {
                    c.sendMessage(new EmbedBuilder()
                        .setTitle("Queue Commands")
                        .addField("list", "list songs currently in queue", false)
                        .addField("clear", "clears the queue", false)
                        .addField("remove", "remove song at given position", false)
                        .build()).queue();
                    break;
                    }
                if (args[1].equals("list")) {
                    boolean shortlist = true;
                    try {
                        if (args[2] !=null && args[2].equals("all")) shortlist = false;
                    } catch (ArrayIndexOutOfBoundsException fuckingStupid) {
                        Debug.log("Array error");
                        Debug.log(fuckingStupid.toString());
                    }
                    EmbedBuilder list = new EmbedBuilder();
                    List<Controller.TrackInfo> infoList = Login.audio.q.list();
                    list.setTitle("Queue");
                    String nowplaying = "";
                    if (Login.audio.getPlayingTrack() != null) nowplaying = Login.audio.getPlayingTrack().getInfo().title;
                    Controller.TrackInfo info;
                    if (!nowplaying.isEmpty()) list.addField("Now Playing:", nowplaying, false);
                    for (int i = 0; i < infoList.size(); i++) {
                        info = infoList.get(i);
                        if (nowplaying.equals(info.title)) Debug.log("already playing track"); //do fuckall
                        else list.addField((infoList.indexOf(info) + 1) + ":", info.title, false);
                        if (shortlist && i == 5) i = infoList.size();
                        }
                    c.sendMessage(list.build()).queue();
                    } else if (args[1].equals("clear")) {
                        Login.audio.stop();
                        Login.audio.q.clear();
                        c.sendMessage("Cleared the queue!").queue();
                    } if (args[1].equals("remove")) {
                        if (args[2] != null) {
                            try {
                                int pos = Integer.parseInt(args[2]) - 1;
                                String removedUrl = Login.audio.q.remove(pos);
                                String removedTitle = yt.getTrackInfo(removedUrl).title;
                                c.sendMessage("Removed: " + removedTitle).queue();
                                break;
                            } catch (NumberFormatException ex) {
                                Debug.log(ex.toString());
                            }
                        }
                        c.sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .addField("Usage", "\"(command prefix)queue remove X\"", false)
                        .addField("", "Where X is the position in the queue of the song you wish to remove", false)
                        .build()).queue();
                        break;
                    }
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
