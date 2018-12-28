package com.gladen.beat;

/**
 * COPYRIGHT Gladen Software 2018
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class Controller extends AudioEventAdapter  {
    String desiredChannel = "";
    public Queue q;
    private AudioPlayerManager playerManager;
    private AudioPlayer player;
    private VoiceChannel cChannel;

    public Controller() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        player = playerManager.createPlayer();
        player.addListener(this);
        player.setVolume(Config.volume);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                disconnectVoice();
                Login.Jda.shutdownNow();
            }
        });
        init();
    }

    private boolean connectVoice() {
        if (cChannel == null) {
            if (!desiredChannel.isEmpty()) cChannel = Login.Jda.getVoiceChannelById(desiredChannel);
            //else cChannel = Login.Jda.getVoiceChannelById(Config.voice_channel_id);
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

    @SuppressWarnings("unused")
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
            desiredChannel = "";
        } else {
            //we aren't connected......
        }
    }

    public boolean isVoiceConnected() {
        if (cChannel == null) return false;
        return cChannel.getGuild().getAudioManager().isConnected();
    }

    public void init() {
        q = new Queue();
        System.out.println("Ready to play!");
        /*
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
        */
    }

    public void startPlayback() {
        if (!isVoiceConnected()) connectVoice();
        if (!isPlaying()) nextTrack();
    }

    public void stop() {
        player.stopTrack();
        disconnectVoice();
        Login.Jda.getPresence().setGame(null);
    }

    public void play() {
        if (!isPlaying()) {
            startPlayback();
        } else ; //already playing, no point in interrupting for nothing
        //TODO might merge startPlayback() and this function together
    }

    public void play(String url) {
        if (!isPlaying()) {
            if (!isVoiceConnected()) connectVoice();
            loadTrack(url);
        } else {
            loadTrack(url); //TODO add a setting for interrupt
        }
    }

    public void skip() {
        nextTrack();
    }

    public void seek(long time) {
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


    protected void loadTrack(String song) {
        playerManager.loadItem(song, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                System.out.println("[Now Playing] " + track.getInfo().title);
                player.startTrack(track, false);
                Login.Jda.getPresence().setGame(Game.playing("▶ " + track.getInfo().title));
                String uri = track.getInfo().uri;
                if (!q.queue.contains(uri)) q.queue.add(uri);
                if (!callbacks.isEmpty()) {
                    for (AudioLoadResultCallback callback : callbacks) {
                        callback.trackLoaded(track);
                    }
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                nextTrack();
            }

            @Override
            public void noMatches() {
                nextTrack();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (!callbacks.isEmpty()) {
                    for (AudioLoadResultCallback callback : callbacks) {
                        callback.loadFailed(exception);
                    }
                }
            }
        });
    }

    protected void nextTrack() {
        //String song = getRandomSong(queue);
        String song = q.queue.get(0);
        loadTrack(song);
        q.queue.remove(0);
        q.queue.add(song); //for endless mode
    }



    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        nextTrack();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        nextTrack();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }






    protected void proccessTracks(List<String> input) {
        System.out.println("Processing " + input.size() + " songs on current playlist");
        for (String song : new ArrayList<>(input)) {
            if (isPlaylist(song)) {
                parsePlaylist(song, input);
            }
        }
        if (input.isEmpty()) {
            System.err.println("No supported songs found!");
        }
        System.out.println(input.size() + " songs loaded");
    }

    private void parsePlaylist(String song, List<String> input) {
        System.out.println("Found a playlist, parsing");
        try {
            playerManager.loadItem(song, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    input.remove(song);
                    for (AudioTrack tr : playlist.getTracks()) {
                        String uri = tr.getInfo().uri;
                        if (!input.contains(uri)) input.add(uri);
                    }
                    System.out.println("Parsed playlist with " + playlist.getTracks().size() + " songs");
                }

                @Override
                public void noMatches() {
                    input.remove(song);
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    input.remove(song);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean isPlaylist(String song) {
        for (Pattern pattern : Patterns.validTrackPatterns) {
            if (pattern.matcher(song).matches()) {
                return Patterns.playlistEmbeddedPattern.matcher(song).find() || Patterns.mixEmbeddedPattern.matcher(song).find();
            }
        }
        return true;
    }



    private List<AudioLoadResultCallback> callbacks = new ArrayList<>();

    protected void registerEventListener(AudioLoadResultCallback callback) {
        this.callbacks.add(callback);
    }

    protected void unregisterEventListener(AudioLoadResultCallback callback) {
        this.callbacks.remove(callback);
    }

    protected void unregisterAllListeners() {
        for (AudioLoadResultCallback callback : this.callbacks) {
            this.callbacks.remove(callback);
        }
    }


    public class Queue {

        private List<String> queue = new ArrayList<>();
        final static String property = "java.io.tmpdir";
        final String tempDir = System.getProperty(property);
        final String cacheFile = tempDir + "beatCache";

        public Queue() {
            //TODO load queue from disk
            System.err.println("OS current temporary directory is " + tempDir);
            File file = new File(cacheFile);
            if (!file.exists()) file = new File("songs.txt"); //TODO We should do both if we're going to keep songs.txt

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    queue.add(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                System.err.println("Could not find songs.txt");
            }
            proccessTracks(queue);
        }

        public List<TrackInfo> list() {
            List<TrackInfo> results = new ArrayList<>();
            YT yt = new YT();
            if (queue.isEmpty()) {
                TrackInfo empty = new TrackInfo();
                empty.title = "No songs in queue";
                empty.pos = -1;
                results.add(empty);
                return results;
            }
            for (String song : queue) {
                TrackInfo info = yt.getTrackInfo(song);
                info.pos = queue.indexOf(song);
                info.uri = song;
                results.add(info);
            }
            return results;
        }

        public String remove(int position) {
            onModify();
            return queue.remove(position);
        }

        public void add(String url) {
            queue.add(url);
            onModify();
        }

        public void clear() {
            queue.clear();
            onModify();
        }

        public void onModify() {
            //TODO write playlist to disk
            File cFile = new File(cacheFile);

            try {
                if (cFile.exists()) cFile.delete();
                cFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(cFile));
                for(String songUrl : queue) {
                    writer.write(songUrl);
                    writer.newLine();
                    }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class TrackInfo {
        String title,id,uri;
        int pos;
    }

}