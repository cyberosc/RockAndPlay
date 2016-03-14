package com.acktos.playcoffe.models;

/**
 * Created by Acktos on 12/4/15.
 */
public class Card {

    private String code;
    private String barId;
    private String credits;
    private String expirationForUse;
    private String expirationForScan;
    private String generated;
    private String state;
    private String usedBy;

    public Card(){
        // empty constructor for firebase deserialize
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBarId() {
        return barId;
    }

    public void setBarId(String barId) {
        this.barId = barId;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getExpirationForUse() {
        return expirationForUse;
    }

    public void setExpirationForUse(String expirationUse) {
        this.expirationForUse = expirationUse;
    }

    public String getExpirationForScan() {
        return expirationForScan;
    }

    public void setExpirationForScan(String expirationForScan) {
        this.expirationForScan = expirationForScan;
    }

    public String getGenerated() {
        return generated;
    }

    public void setGenerated(String generated) {
        this.generated = generated;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }
}
