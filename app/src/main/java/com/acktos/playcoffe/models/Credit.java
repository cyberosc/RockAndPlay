package com.acktos.playcoffe.models;

/**
 * Created by Acktos on 1/25/16.
 */
public class Credit {

    private String value;
    private String expired;
    private String type;
    private String earnDate;


    public Credit(){
        // empty constructor for firebase deserialize
    }

    public Credit(String value,String expired,String type,String earnDate){

        setExpired(expired);
        setValue(value);
        setType(type);
        setEarnDate(earnDate);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEarnDate() {
        return earnDate;
    }

    public void setEarnDate(String earnDate) {
        this.earnDate = earnDate;
    }
}
