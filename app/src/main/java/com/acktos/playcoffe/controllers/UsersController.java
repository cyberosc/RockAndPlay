package com.acktos.playcoffe.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.acktos.playcoffe.android.AppController;
import com.acktos.playcoffe.android.InternalStorage;
import com.acktos.playcoffe.models.User;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Acktos on 8/12/15.
 */
public class UsersController extends  BaseController{


    //Components
    private InternalStorage internalStorage;

    //Constants
    public static final String TAG_GET_USER_SPOTIFY="get_user_spotify";


    public UsersController (Context context){

        internalStorage=new InternalStorage(context);
    }

    public String getSpotifyUser(
            final String accessToken,
            final Response.Listener<User> responseListener,
            final Response.ErrorListener errorListener){


        StringRequest jsonObjReq = new StringRequest(

                Request.Method.GET,
                API.GET_SPOTIFY_USER.getUrl(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "spotify login:" + response.toString());

                        User user=extractSpotifyUser(response,accessToken);
                        saveUserProfile(user);
                        responseListener.onResponse(user);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        errorListener.onErrorResponse(error);
                    }
                }){

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();

                params.put("Authorization", "Bearer "+accessToken);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG_GET_USER_SPOTIFY);
        return null;
    }

    /**
     * save profile into internal storage
     */
    public void saveUserProfile(User user){

        internalStorage.saveFile(FILE_USER_PROFILE, user.toString());

    }

    /**
     * Get user profile from internal storage
     * @return User
     */
    public User getUser(){

        User user=null;

        if(internalStorage.isFileExists(FILE_USER_PROFILE)){

            try {
                String profileString=internalStorage.readFile(FILE_USER_PROFILE);

                Log.i(TAG,"profile: "+profileString);
                JSONObject jsonObject=new JSONObject(profileString);

                user=new User(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    private User extractSpotifyUser(String jsonString,String accessToken){

        final String KEY_ID="id";
        final String KEY_NAME="display_name";
        final String KEY_BIRTHDATE="birthdate";
        final String KEY_EMAIL="email";
        final String KEY_IMAGES="images";

        User user=null;
        try {
            JSONObject jsonObject=new JSONObject(jsonString);

            String email=jsonObject.getString(KEY_EMAIL);
            String birthdate=jsonObject.getString(KEY_BIRTHDATE);
            String name=jsonObject.getString(KEY_NAME);
            if(name=="null" || TextUtils.isEmpty(name)){
                name=jsonObject.getString(KEY_ID);
            }
            user=new User(name,email,null,"spotify");
            user.setBirthdate(birthdate);


        } catch (JSONException e) {
            Log.e(TAG, "json syntax error:"+e.getMessage());
            e.printStackTrace();
        }
        return user;
    }


}
