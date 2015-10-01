package com.acktos.playcoffe.models;

/**
 * Created by Acktos on 9/17/15.
 */
public class Player extends BaseModel {

    private String trackName;
    private String artistName;
    private String thumbAlbum;
    private String duration;
    private String state;
    private String message;
    private String startTime;
    private String pauseTime;
    private String endTime;



    public static final String STATE_PLAY="play";
    public static final String STATE_PAUSE="pause";
    public static final String STATE_STOP="stop";

    public Player(){}

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getThumbAlbum() {
        return thumbAlbum;
    }

    public void setThumbAlbum(String thumbAlbum) {
        this.thumbAlbum = thumbAlbum;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(String pauseTime) {
        this.pauseTime = pauseTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


}
