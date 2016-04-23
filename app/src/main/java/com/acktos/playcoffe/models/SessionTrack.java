package com.acktos.playcoffe.models;

/**
 * Created by Acktos on 3/14/16.
 */
public class SessionTrack {


    private String id;
    private String trackId;
    private String userId;
    private String order;
    private String state;

    public SessionTrack(){
    }

    public SessionTrack(String trackId,String userId){

        setTrackId(trackId);
        setUserId(userId);
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
