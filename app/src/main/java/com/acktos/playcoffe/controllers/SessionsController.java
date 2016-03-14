package com.acktos.playcoffe.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.acktos.playcoffe.android.DateTimeUtils;
import com.acktos.playcoffe.android.InternalStorage;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 9/29/15.
 */
public class SessionsController extends BaseController{

    Context context;

    //Components
    private InternalStorage internalStorage;

    public SessionsController(Context context){

        this.context=context;
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

                Log.i(TAG, "session: " + sessionString);
                JSONObject jsonObject=new JSONObject(sessionString);

                session=new Session(jsonObject);

            } catch (JSONException e) {
                Log.e(TAG,"error trying retrieved joined session file");
                e.printStackTrace();
            }
        }

        return session;
    }

    public void deleteJoinedSession(){

        context.deleteFile(FILE_JOINED_SESSION);
    }

    /**
     * This method checks if user belongs to a current session.
     * @return
     */
    public boolean isUserJoinedToSession(){

        Session session=getJoinedSession();

        if(session!=null){

            String currentDate=DateTimeUtils.getCurrentTime();
            int sessionDateComparator= DateTimeUtils.compareTwoDateStrings(currentDate,session.getFinalDate());

            if(sessionDateComparator==0){

                // Error to compare dates
                Log.i(BaseController.TAG,"Error to compare dates in getJoinedSession()");
                return false;
            }else if(sessionDateComparator>0){

                Log.i(BaseController.TAG,"Session is over");
                return false;
            }else if(sessionDateComparator<0){

                // user belongs to a current session.
                Log.i(BaseController.TAG,"user belongs to a current session");
                return true;
            }
        }

        return false;

    }
}
