package com.acktos.playcoffe.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.acktos.playcoffe.android.InternalStorage;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 9/29/15.
 */
public class SessionsController extends BaseController{


    //Components
    private InternalStorage internalStorage;

    public SessionsController(Context context){

        internalStorage=new InternalStorage(context);
    }

    public void saveJoinedSession(Session session){

        internalStorage.saveFile(FILE_JOINED_SESSION,session.toString());
    }

    public Session getJoinedSession(){

        Session session=null;

        if(internalStorage.isFileExists(FILE_JOINED_SESSION)){

            try {
                String sessionString=internalStorage.readFile(FILE_JOINED_SESSION);

                Log.i(TAG, "profile: " + sessionString);
                JSONObject jsonObject=new JSONObject(sessionString);

                session=new Session(jsonObject);

            } catch (JSONException e) {
                Log.e(TAG,"error trying retrieved joined session file");
                e.printStackTrace();
            }
        }

        return session;
    }

    public boolean isUserJoinedToSession(){

        if(getJoinedSession()!=null){
            return true;
        }else{
            return false;
        }
    }
}
