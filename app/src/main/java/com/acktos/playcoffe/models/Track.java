package com.acktos.playcoffe.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 3/14/16.
 */
public class Track {

    private String id;
    private String artist;
    private String duration;
    private String durationText;
    private String file;
    private String song;

    public static final String KEY_ID="id";
    public static final String KEY_ARTIST="artist";
    public static final String KEY_DURATION="duration";
    public static final String KEY_DURATION_TEXT="durationText";
    public static final String KEY_FILE="file";
    public static final String KEY_SONG="song";

    public Track(){}

    public String getArtist() {
        return artist;
    }

    public Track(String id,String artist,String duration,String durationText,String song){

        setId(id);
        setArtist(artist);
        setDuration(duration);
        setDurationText(durationText);
        setSong(song);

    }

    @Override
    public String toString(){

        JSONObject jsonObject=new JSONObject();

        try{
            if(id!=null){
                jsonObject.put(KEY_ID,id);
            }
            if(artist!=null){
                jsonObject.put(KEY_ARTIST,artist);
            }
            if(duration!=null){
                jsonObject.put(KEY_DURATION,duration);
            }
            if(durationText!=null){
                jsonObject.put(KEY_DURATION_TEXT,durationText);
            }
            if(file!=null){
                jsonObject.put(KEY_FILE,file);
            }
            if(song!=null){
                jsonObject.put(KEY_SONG,song);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
