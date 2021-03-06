package com.acktos.playcoffe.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 8/31/15.
 */
public class Session extends BaseModel{

    public String id;
    public String barId;
    public String sessionName;
    public String startDate;
    public String endDate;


    public static final String KEY_BAR_ID="barId";
    public static final String KEY_SESSION_NAME="sessionName";
    public static final String KEY_START_DATE="startDate";
    public static final String KEY_END_DATE="endDate";
    public static final String KEY_JOINED_USERS="joinedUsers";


    public Session(){}

    public Session (
            String id,String barId,String sessionName,String startDate,String endDate){

        this.id=id;
        this.barId=barId;
        this.sessionName=sessionName;
        this.startDate=startDate;
        this.endDate=endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }



    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinalDate() {
        return endDate;
    }

    public void setFinalDate(String finalDate) {
        this.endDate = finalDate;
    }

    public String getBarId() {
        return barId;
    }

    public void setBarId(String barId) {
        this.barId = barId;
    }


    public Session (JSONObject jsonObject){

        try{
            if(jsonObject.has(KEY_ID)) {
                this.id = jsonObject.getString(KEY_ID);
            }
            if(jsonObject.has(KEY_BAR_ID)) {
                this.barId = jsonObject.getString(KEY_BAR_ID);
            }
            if(jsonObject.has(KEY_SESSION_NAME)) {
                this.sessionName = jsonObject.getString(KEY_SESSION_NAME);
            }

            if(jsonObject.has(KEY_START_DATE)) {
                this.startDate = jsonObject.getString(KEY_START_DATE);
            }
            if(jsonObject.has(KEY_END_DATE)) {
                this.endDate = jsonObject.getString(KEY_END_DATE);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public String toString(){

        JSONObject jsonObject=new JSONObject();

        try{
            if(id!=null){
                jsonObject.put(KEY_ID,id);
            }
            if(barId!=null){
                jsonObject.put(KEY_BAR_ID,barId);
            }
            if(sessionName!=null){
                jsonObject.put(KEY_SESSION_NAME,sessionName);
            }

            if(startDate!=null){
                jsonObject.put(KEY_START_DATE,startDate);
            }
            if(endDate!=null){
                jsonObject.put(KEY_END_DATE,endDate);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

}
