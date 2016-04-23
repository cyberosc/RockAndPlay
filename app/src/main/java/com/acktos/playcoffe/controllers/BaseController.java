package com.acktos.playcoffe.controllers;

import com.acktos.playcoffe.models.Player;
import com.acktos.playcoffe.models.SpotifyTrack;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 8/11/15.
 */
public class BaseController {

    public static final String TAG="playcoffe-debug";
    public static final String SHARED_PREFERENCES="com.acktos.playcoffe.sharedpreferences";
    public static final String FILE_USER_PROFILE="com.acktos.playcoffe.USER_PROFILE";
    public static final String FILE_JOINED_SESSION="com.acktos.roackandplay.JOINED_SESSION";
    public static final String FILE_BAR_PLAY_LIST="com.acktos.roackandplay.BAR_PLAY_LIST";


    //FireBase Credentials
    public static final String FIREBASE_URL="https://coffe-play.firebaseio.com/";
    public static final String TABLE_USERS="users";
    public static final String TABLE_SESSIONS="sessions";
    public static final String TABLE_CREDITS="credits";
    public static final String TABLE_TRACKS="tracks";
    public static final String TABLE_CARDS="cards";
    public static final String TABLE_BARS="bars";
    public static final String TABLE_PLAYER_STATE="playerState";
    public static final String TABLE_PLAYLIST="playlist";

    // Api endpoints

    public enum API{

        GET_SPOTIFY_USER("http://www.acktos.com.co/prueba_oscar.php"),
        SEARCH_TRACK_SPOTIFY("https://api.spotify.com/v1/search");

        private final String url;

        API (String uri){
            url=uri;
        }

        public String getUrl(){
            return url;
        }
    }

    public static String getProfileImageUrl(String jsonString){

        String profilePic=null;

        try {
            JSONObject jsonObject=new JSONObject(jsonString);
            profilePic=jsonObject.getString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return profilePic;
    }
}
