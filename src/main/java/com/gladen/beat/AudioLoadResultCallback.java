package com.gladen.beat;

/**
 * COPYRIGHT Gladen Software 2018
 */

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface AudioLoadResultCallback {

    public void trackLoaded(AudioTrack track);
    public void loadFailed(FriendlyException ex);
}