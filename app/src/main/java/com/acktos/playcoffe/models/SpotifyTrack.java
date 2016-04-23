package com.acktos.playcoffe.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 9/3/15.
 */

@Deprecated
public class SpotifyTrack extends BaseModel{

    private String id;
    private String name;
    private String artist;
    private String album;
    private String thumb;
    private String image;
    private String duration;


    public static final String KEY_NAME="name";
    public static final String KEY_ARTIST="artist";
    public static final String KEY_ALBUM="album";
    public static final String KEY_THUMB="thumb";
    public static final String KEY_IMAGE="image";
    public static final String KEY_DURATION="duration";
    public static final String KEY_TRACK_ID="track_id";
    public static final String KEY_STATE="state";
    public static final String KEY_ORDER="order";


    public static final String KEY_SPOTIFY_NAME="name";
    public static final String KEY_SPOTIFY_DURATION="duration_ms";
    public static final String KEY_SPOTIFY_ARTIST="artists";
    public static final String KEY_SPOTIFY_ALBUM="album";
    public static final String KEY_SPOTIFY_IMAGE="images";
    public static final String KEY_SPOTIFY_URL="url";
    public static final String KEY_SPOTIFY_ITEMS="items";

    // states for joined play list tracks
    public static final String STATE_TRACK_PENDING="pending";
    public static final String STATE_TRACK_APPROVED="approved";

    public SpotifyTrack(){}

    public SpotifyTrack(String id, String name, String artist, String album, String thumb, String image, String duration){

        setId(id);
        setName(name);
        setArtist(artist);
        setAlbum(album);
        setThumb(thumb);
        setImage(image);
        setDuration(duration);
    }

    @Override
    public String toString(){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(KEY_ID,getId());
            jsonObject.put(KEY_NAME,getName());
            jsonObject.put(KEY_ARTIST,getArtist());
            jsonObject.put(KEY_ALBUM,getAlbum());
            jsonObject.put(KEY_THUMB,getThumb());
            jsonObject.put(KEY_IMAGE,getImage());
            jsonObject.put(KEY_DURATION,getDuration());
        } catch (JSONException e) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


}
