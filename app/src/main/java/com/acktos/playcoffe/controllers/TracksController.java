package com.acktos.playcoffe.controllers;

import android.content.Context;
import android.util.Log;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.AppController;
import com.acktos.playcoffe.android.InternalStorage;
import com.acktos.playcoffe.models.BaseModel;
import com.acktos.playcoffe.models.QueueTrack;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.models.SessionTrack;
import com.acktos.playcoffe.models.SpotifyTrack;
import com.acktos.playcoffe.models.Track;
import com.acktos.playcoffe.models.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Acktos on 9/3/15.
 */
public class TracksController extends BaseController{

    //Components
    private InternalStorage internalStorage;

    //Constants
    public static final String TAG_SEARCH_TRACK_SPOTIFY="search_track_spotify";


    //Attributes
    private Context context;
    Session session;
    SessionsController sessionsController;
    
    //Android Utils
    Gson gson;


    public TracksController(Context context){

        this.context=context;
        internalStorage=new InternalStorage(context);
        sessionsController=new SessionsController(context);
        session=sessionsController.getJoinedSession();
        gson=new Gson();
    }


    public String searchTracksFromBarPlayList(
            final String query, final Response.Listener<List<SpotifyTrack>> responseListener,
            final Response.ErrorListener errorListener){

        // Get a RequestQueue
        RequestQueue queue = AppController.getInstance().getRequestQueue();

        if(queue!=null){

            queue.cancelAll(TAG_SEARCH_TRACK_SPOTIFY);
            Log.i(TAG, "all search request were cancelled");
        }

        String queryEncode = null;
        try {
            queryEncode = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String searchURL = String.format(API.SEARCH_TRACK_SPOTIFY.getUrl()+"?q=%1$s&type=track", queryEncode);

        Log.i(BaseController.TAG,searchURL);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(

                Request.Method.GET,
                searchURL,
                null,
                new Response.Listener<JSONObject>() {

                    List<SpotifyTrack> tracks=null;
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "spotify search:" + response.toString());

                        tracks=new ArrayList<>();
                        try {
                            JSONArray jsonArrayItems=response.getJSONObject("tracks").getJSONArray(SpotifyTrack.KEY_SPOTIFY_ITEMS);
                            for (int i=0; i<jsonArrayItems.length();i++){
                                JSONObject jsonObjectItem=jsonArrayItems.getJSONObject(i);
                                tracks.add(getTrackFromSpotifyObject(jsonObjectItem));

                                //Log.i(TAG, "spotify track:" + getTrackFromSpotifyObject(jsonObjectItem).toString());
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        responseListener.onResponse(tracks);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e(TAG, "spotify error search:" + error.networkResponse);
                        Log.e(TAG, "spotify error search:" + error.getStackTrace());
                        error.printStackTrace();
                        errorListener.onErrorResponse(error);
                    }
                });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG_SEARCH_TRACK_SPOTIFY);
        return null;

    }


    public String searchSpotifyTracks(
            final String query, final Response.Listener<List<SpotifyTrack>> responseListener,
            final Response.ErrorListener errorListener){

        // Get a RequestQueue
        RequestQueue queue = AppController.getInstance().getRequestQueue();

        if(queue!=null){

            queue.cancelAll(TAG_SEARCH_TRACK_SPOTIFY);
            Log.i(TAG, "all search request were cancelled");
        }

        String queryEncode = null;
        try {
            queryEncode = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String searchURL = String.format(API.SEARCH_TRACK_SPOTIFY.getUrl()+"?q=%1$s&type=track", queryEncode);

        Log.i(BaseController.TAG,searchURL);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(

                Request.Method.GET,
                searchURL,
                null,
                new Response.Listener<JSONObject>() {

                    List<SpotifyTrack> tracks=null;
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "spotify search:" + response.toString());

                        tracks=new ArrayList<>();
                        try {
                            JSONArray jsonArrayItems=response.getJSONObject("tracks").getJSONArray(SpotifyTrack.KEY_SPOTIFY_ITEMS);
                            for (int i=0; i<jsonArrayItems.length();i++){
                                JSONObject jsonObjectItem=jsonArrayItems.getJSONObject(i);
                                tracks.add(getTrackFromSpotifyObject(jsonObjectItem));

                                //Log.i(TAG, "spotify track:" + getTrackFromSpotifyObject(jsonObjectItem).toString());
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        responseListener.onResponse(tracks);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e(TAG, "spotify error search:" + error.networkResponse);
                        Log.e(TAG, "spotify error search:" + error.getStackTrace());
                        error.printStackTrace();
                        errorListener.onErrorResponse(error);
                    }
                });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG_SEARCH_TRACK_SPOTIFY);
        return null;

    }

    public List<Track> searchTrackFromLocalPlayList(String query){

        query=query.toLowerCase();

        List<Track> results=new ArrayList<>();
        List<Track> barPlayList=getLocalPlayList();

        for (Track trackObject:barPlayList) {

            String artist=trackObject.getArtist().toLowerCase();
            String song=trackObject.getSong().toLowerCase();

            if(artist.contains(query)){
                results.add(trackObject);
            }
            else if(song.contains(query)){
                results.add(trackObject);
            }
        }

        return results;
    }

    /**
     * Transform json string object from spotify search api to models.Track Object
     */
    private SpotifyTrack getTrackFromSpotifyObject(JSONObject itemObject){

        SpotifyTrack track=null;

        try {

            String name=itemObject.getString(SpotifyTrack.KEY_SPOTIFY_NAME);
            String id=itemObject.getString(BaseModel.KEY_ID);
            String duration=itemObject.getString(SpotifyTrack.KEY_SPOTIFY_DURATION);

            JSONObject jsonAlbum=itemObject.getJSONObject(SpotifyTrack.KEY_SPOTIFY_ALBUM);
            JSONArray jsonArrayArtists=itemObject.getJSONArray(SpotifyTrack.KEY_SPOTIFY_ARTIST);
            String album=jsonAlbum.getString(SpotifyTrack.KEY_SPOTIFY_NAME);
            String thumb=jsonAlbum.getJSONArray(SpotifyTrack.KEY_SPOTIFY_IMAGE).getJSONObject(2).getString(SpotifyTrack.KEY_SPOTIFY_URL);
            String image=jsonAlbum.getJSONArray(SpotifyTrack.KEY_SPOTIFY_IMAGE).getJSONObject(0).getString(SpotifyTrack.KEY_SPOTIFY_URL);
            String artist=jsonArrayArtists.getJSONObject(0).getString(SpotifyTrack.KEY_SPOTIFY_NAME);

            track =new SpotifyTrack(id,name,artist,album,thumb,image,duration);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return track;

    }

    public void saveBarPlayList(String barPlayList){

        internalStorage.saveFile(FILE_BAR_PLAY_LIST, barPlayList);
    }
    
    public List<Track> getLocalPlayList(){
        
        List<Track> localPlayList=new ArrayList<>();

        if(internalStorage.isFileExists(FILE_BAR_PLAY_LIST)){
            
            String localPlayListString=internalStorage.readFile(FILE_BAR_PLAY_LIST);

            Log.i(TAG, "getLocalPlayList:"+localPlayListString);

            Type listType = new TypeToken<List<Track>>(){}.getType();
            localPlayList=gson.fromJson(localPlayListString, listType);

           
        }

        return localPlayList;
    }

    /**
     * Check number of queue tracks.
     */
    public void addTrackToQueue(final SessionTrack sessionTrack,final OnAddTrackListener addTrackListener){


        final int sessionLimitQueue=100; // TODO: Get session limit queue
        final Firebase tracksRef = new Firebase(
                BaseController.FIREBASE_URL+BaseController.TABLE_SESSIONS+"/"+session.getId()+"/"+BaseController.TABLE_TRACKS);

        tracksRef.addListenerForSingleValueEvent(new ValueEventListener() {

            boolean success = false;
            String message;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long queueSize = snapshot.getChildrenCount();

                if (queueSize < sessionLimitQueue) {

                    Firebase newTrackRef = tracksRef.push();
                    //Map<String, String> trackMap = new HashMap<String, String>();
                    //trackMap.put(SpotifyTrack.KEY_TRACK_ID,trackId);
                    //trackMap.put(SpotifyTrack.KEY_STATE, SpotifyTrack.STATE_TRACK_APPROVED);
                    //trackMap.put(SpotifyTrack.KEY_ORDER,(queueSize+1)+"");

                    sessionTrack.setOrder((queueSize + 1) + "");
                    sessionTrack.setState("approved");//TODO: change constant location to QueueTrack class


                    newTrackRef.setValue(sessionTrack, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                success = false;
                                message = context.getString(R.string.msg_add_track_failed);
                                Log.e(TAG, "Data could not be saved. " + firebaseError.getMessage());
                            } else {
                                success = true;
                                Log.i(TAG, "Track queue saved successfully");
                            }

                            Log.e(TAG, "addTrackToQueue success:" + success);
                            Log.e(TAG, "addTrackToQueue message:" + message);
                            addTrackListener.onAddTrack(message, success);
                        }
                    });

                } else {
                    success = false;
                    message = context.getString(R.string.msg_max_number_tracks_reached);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public List<Track> getBarTracksFromSessionTracks(List<SessionTrack> sessionTracks){

        List<Track> barTracksList=null;

        if(internalStorage.isFileExists(FILE_BAR_PLAY_LIST)){

            try {
                String barPlayListString=internalStorage.readFile(FILE_BAR_PLAY_LIST);

                JSONArray barTracksJsonArray=new JSONArray(barPlayListString);

                barTracksList=new ArrayList<>();

                for(int i=0; i<barTracksJsonArray.length();i++){

                    JSONObject trackObject=barTracksJsonArray.getJSONObject(i);


                    if(isTrackOnSessionTracks(trackObject.getString(Track.KEY_ID), sessionTracks)){

                        Track track=new Track(
                                trackObject.getString(Track.KEY_ID),
                                trackObject.getString(Track.KEY_ARTIST),
                                trackObject.getString(Track.KEY_DURATION),
                                trackObject.getString(Track.KEY_DURATION_TEXT),
                                trackObject.getString(Track.KEY_SONG));


                        barTracksList.add(track);

                        Log.i(TAG, "getBarTracksFromSessionTracks: find song:"+trackObject.getString(Track.KEY_SONG));
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "getBarTracksFromSessionTracks: jsonException");
            }
        }else{
            Log.i(TAG, "getBarTracksFromSessionTracks: bar play list file don't exists");
        }

        return barTracksList;

    }

    private boolean isTrackOnSessionTracks(String trackId, List<SessionTrack> sessionTracks){

        if(sessionTracks!=null){

            for(int i=0;i<sessionTracks.size();i++){

                String sessionTrackId=sessionTracks.get(i).getTrackId();

                if(sessionTrackId.equals(trackId)){
                    return true;
                }
            }


        }else{
            Log.i(TAG, "isTrackOnSessionTracks: sessionTracks is null");
        }

        return false;
    }

    public interface OnAddTrackListener{
        public void onAddTrack(String message,boolean success);
    }



}
