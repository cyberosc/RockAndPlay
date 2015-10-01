package com.acktos.playcoffe.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 8/11/15.
 */
public class User extends BaseModel{



    public String id;
    public String name;
    public String email;
    public String birthdate;
    public String profilePic;
    public String accessToken;
    public String provider;
    public String session;

    public static final String KEY_NAME="name";
    public static final String KEY_EMAIL="email";
    public static final String KEY_BIRTHDATE="birthdate";
    public static final String KEY_PROFILE_PIC="profile_pic";
    public static final String KEY_ACCESS_TOKEN="access_token";
    public static final String KEY_PROVIDER="provider";
    public static final String KEY_SESSION="session";



    public User(){}

    public User(String name,String email, String profilePic,String provider){

        setName(name);
        setEmail(email);
        setProfilePic(profilePic);
        setProvider(provider);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }



    public User (JSONObject jsonObject){

        try{
            if(jsonObject.has(KEY_ID)) {
                this.id = jsonObject.getString(KEY_ID);
            }
            if(jsonObject.has(KEY_NAME)) {
                this.name = jsonObject.getString(KEY_NAME);
            }
            if(jsonObject.has(KEY_EMAIL)) {
                this.email = jsonObject.getString(KEY_EMAIL);
            }
            if(jsonObject.has(KEY_BIRTHDATE)) {
                this.birthdate = jsonObject.getString(KEY_BIRTHDATE);
            }
            if(jsonObject.has(KEY_PROFILE_PIC)) {
                this.profilePic = jsonObject.getString(KEY_PROFILE_PIC);
            }
            if(jsonObject.has(KEY_ACCESS_TOKEN)) {
                this.accessToken = jsonObject.getString(KEY_ACCESS_TOKEN);
            }
            if(jsonObject.has(KEY_PROVIDER)) {
                this.provider = jsonObject.getString(KEY_PROVIDER);
            }
            if(jsonObject.has(KEY_SESSION)) {
                this.session = jsonObject.getString(KEY_SESSION);
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
            if(name!=null){
                jsonObject.put(KEY_NAME,name);
            }
            if(email!=null){
                jsonObject.put(KEY_EMAIL,email);
            }
            if(birthdate!=null){
                jsonObject.put(KEY_BIRTHDATE,birthdate);
            }
            if(profilePic!=null){
                jsonObject.put(KEY_PROFILE_PIC,profilePic);
            }
            if(accessToken!=null){
                jsonObject.put(KEY_ACCESS_TOKEN,accessToken);
            }
            if(provider!=null){
                jsonObject.put(KEY_PROVIDER,provider);
            }
            if(session!=null){
                jsonObject.put(KEY_SESSION,session);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
