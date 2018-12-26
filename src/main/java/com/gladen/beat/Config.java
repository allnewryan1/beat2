package com.gladen.beat;

import com.kaaz.configuration.ConfigurationOption;

public class Config {
    @ConfigurationOption
    public static String discord_token = "your-discord-token";

    @ConfigurationOption
    public static String command_prefix = "!";

    @ConfigurationOption
    public static int volume = 30;

    @ConfigurationOption
    public static String voice_channel_id = "channel-id";

    @ConfigurationOption
    public static String yt_api_key = "your-youtube-api-key";
}
