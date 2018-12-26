package com.gladen.beat;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class Controller {
    static TrackScheduler scheduler;
    private AudioPlayerManager playerManager;
    private AudioPlayer player;
    private VoiceChannel cChannel;

    public Controller() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        player = playerManager.createPlayer();
        System.out.println("Created new player manager");
        init();
    }

    private boolean connectVoice() {
        //Get rid of this, use name select instead #Jda.getVoiceChannelByName()
        if (cChannel == null) {
            cChannel = Login.Jda.getVoiceChannelById(Config.voice_channel_id);
            if (cChannel == null) {
                System.out.println("Could not find hardcoded channel, make sure the ID is correct and that the bot can see it.");
                return false;
            }
        }
        AudioManager audioManager = cChannel.getGuild().getAudioManager();
        try {
            audioManager.openAudioConnection(cChannel);
            System.out.println("Joined voice channel " + cChannel.getName());
            audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
            return true;
        } catch (Exception ex) {
            System.out.println("Failed to join the voice channel! " + ex.getMessage());
            return false;
        }
    }

    private boolean connectVoice(String server) {
        cChannel = Login.Jda.getVoiceChannelById(server);
        AudioManager audioManager = cChannel.getGuild().getAudioManager();
        try {
            audioManager.openAudioConnection(cChannel);
            System.out.println("Joined voice channel " + cChannel.getName());
            audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
            return true;
        } catch (Exception ex) {
            System.out.println("Failed to join the voice channel! " + ex.getMessage());
            return false;
        }
    }

    private void disconnectVoice() {
        if (cChannel == null) return;
        AudioManager audioManager = cChannel.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
            audioManager.setSendingHandler(null);
        } else {
            //we aren't connected......
        }
    }

    public boolean isVoiceConnected() {
        if (cChannel == null) return false;
        return cChannel.getGuild().getAudioManager().isConnected();
    }

    public void init() {
        if (!connectVoice()) {
            System.out.println("couldn't connect to voice channel");
            System.exit(20);
        }
    }

    public void startPlayback() {
        if (!isVoiceConnected()) connectVoice();
        scheduler = new TrackScheduler(player, playerManager);
        player.addListener(scheduler);
        player.setVolume(Config.volume);
    }

    public boolean stop() {
        player.stopTrack();
        disconnectVoice();
        Login.Jda.getPresence().setGame(null);
        player.removeListener(scheduler);
        scheduler = null;
        return false;
    }

    public boolean play() {
        if (!isPlaying()) {
            startPlayback();
            return true;
        }
        else return false;
    }

    public boolean play(String url) {
        scheduler.loadTrack(url);
        return false;
    }

    public boolean skip() {
        scheduler.nextTrack();
        return false;
    }

    public boolean seek(long time) {
        return false;
    }

    public boolean isPlaying() {
        if (player.getPlayingTrack() != null) return true;
        else return false;
    }

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public void setVolume(int vol) {
        player.setVolume(vol);
    }

    public int getVolume() {
        return player.getVolume();
    }
}