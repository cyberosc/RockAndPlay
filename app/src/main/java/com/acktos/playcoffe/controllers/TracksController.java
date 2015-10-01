package com.acktos.playcoffe.controllers;

import android.content.Context;
import android.util.Log;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.AppController;
import com.acktos.playcoffe.models.BaseModel;
import com.acktos.playcoffe.models.QueueTrack;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.models.SpotifyTrack;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Acktos on 9/3/15.
 */
public class TracksController extends BaseController{


    //Constants
    public static final String TAG_SEARCH_TRACK_SPOTIFY="search_track_spotify";


    //Attributes
    private Context context;
    Session session;
    SessionsController sessionsController;


    public TracksController(Context context){

        this.context=context;
        sessionsController=new SessionsController(context);
        session=sessionsController.getJoinedSession();
    }


    public String searchSpotifyTracks(
            final String query, final Response.Listener<List<SpotifyTrack>> responseListener, final Response.ErrorListener errorListener){

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

    /**
     * Check number of queue tracks, if it
     * @param trackId
     * @param addTrackListener
     */
    public void addTrackToQueue(final QueueTrack queueTrack,final OnAddTrackListener addTrackListener){


        final int sessionLimitQueue=10; // TODO: Get session limit queue
        final Firebase tracksRef = new Firebase(
                BaseController.FIREBASE_URL+BaseController.TABLE_SESSIONS+"/"+session.getId()+"/"+BaseController.TABLE_TRACKS);

        tracksRef.addListenerForSingleValueEvent(new ValueEventListener() {

            boolean success = false;
            String message;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                 long queueSize=snapshot.getChildrenCount();

                if(queueSize<sessionLimitQueue){

                    Firebase newTrackRef=tracksRef.push();
                    //Map<String, String> trackMap = new HashMap<String, String>();
                    //trackMap.put(SpotifyTrack.KEY_TRACK_ID,trackId);
                    //trackMap.put(SpotifyTrack.KEY_STATE, SpotifyTrack.STATE_TRACK_APPROVED);
                    //trackMap.put(SpotifyTrack.KEY_ORDER,(queueSize+1)+"");

                    queueTrack.setOrder((queueSize+1)+"");
                    queueTrack.setState(SpotifyTrack.STATE_TRACK_APPROVED);//TODO: change constant location to QueueTrack class


                    newTrackRef.setValue(queueTrack, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                success=false;
                                message=context.getString(R.string.msg_add_track_failed);
                                Log.e(TAG,"Data could not be saved. " + firebaseError.getMessage());
                            } else {
                                success=true;
                                Log.i(TAG, "Track queue saved successfully");
                            }

                            Log.e(TAG, "addTrackToQueue success:"+success);
                            Log.e(TAG, "addTrackToQueue message:"+message);
                            addTrackListener.onAddTrack(message,success);
                        }
                    });

                }else{
                    success =false;
                    message =context.getString(R.string.msg_max_number_tracks_reached);
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public interface OnAddTrackListener{
        public void onAddTrack(String message,boolean success);
    }

}
