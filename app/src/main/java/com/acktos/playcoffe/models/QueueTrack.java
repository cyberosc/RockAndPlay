package com.acktos.playcoffe.models;

/**
 * Created by Acktos on 9/30/15.
 */

@Deprecated
public class QueueTrack {

    private String id;
    private String trackId;
    private String state;
    private String order;
    private String trackName;
    private String artistName;
    private String duration;
    private String thumbAlbum;
    private String userId;
    private String userName;

    public static final String KEY_TRACK_ID="trackId";
    public static final String KEY_STATE="state";
    public static final String KEY_ORDER="order";
    public static final String KEY_TRACK_NAME="trackName";
    public static final String KEY_ARTIST_NAME="artistName";
    public static final String KEY_DURATION="duration";
    public static final String KEY_THUMB_ALBUM="thumbAlbum";
    public static final String KEY_USER_ID="userId";
    public static final String KEY_USERNAME="userName";

    public QueueTrack(
            String trackId, String trackName, String artistName,
            String duration, String thumbAlbum, String userId, String userName) {

        //this.id = id;
        this.trackId = trackId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.duration = duration;
        this.thumbAlbum = thumbAlbum;
        this.userId = userId;
        this.userName = userName;
    }

    public QueueTrack(
            String id,String trackId, String state, String order, String trackName,
            String artistName, String duration, String thumbAlbum, String userId, String userName) {

        this.id=id;
        this.trackId=trackId;
        this.state = state;
        this.order = order;
        this.trackName = trackName;
        this.artistName = artistName;
        this.duration = duration;
        this.thumbAlbum = thumbAlbum;
        this.userId = userId;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
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

    public String getThumbAlbum() {
        return thumbAlbum;
    }

    public void setThumbAlbum(String thumbAlbum) {
        this.thumbAlbum = thumbAlbum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
