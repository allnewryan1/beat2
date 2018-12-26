package com.gladen.beat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YT {
    public YT() {

    }
    final String queryUrl = "https://www.googleapis.com/youtube/v3/search";
    final String playUrl = "https://www.youtube.com/watch?v=";

    public String query(String text) {
        if (text == null || text.isEmpty()) return "";
        try {
            String url = queryUrl + "?part=id" + "&type=video" + "&q=" + URLEncoder.encode(text, "UTF-8") + "&key=" + Config.yt_api_key;
            URL obj = new URL(url);
		    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		    con.setRequestMethod("GET");
            con.connect();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                InputStream input = con.getInputStream();

                BufferedReader bR = new BufferedReader(new InputStreamReader(input));
                String line = "";
                StringBuilder sb = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    sb.append(line);
                    }
                input.close();
                con.disconnect();
                input = null;
                con = null;

                JSONObject result = new JSONObject(sb.toString());
                JSONArray list = result.getJSONArray("items");
                JSONObject first = list.getJSONObject(0);
                JSONObject id = first.getJSONObject("id");
                String finalUrl = "";
                try {
                    finalUrl = playUrl + id.getString("videoId");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "";
                }
                return finalUrl;
            } else if (responseCode == 400) {

                InputStream input = con.getErrorStream();

                BufferedReader bR = new BufferedReader(new InputStreamReader(input));
                String line = "";
                StringBuilder sb = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    sb.append(line);
                    }
                input.close();
                con.disconnect();
                input = null;
                con = null;
                System.out.println(sb.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

}