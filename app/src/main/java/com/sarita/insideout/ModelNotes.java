package com.sarita.insideout;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ModelNotes {
    String title, description, owner;
    Timestamp TimeStamp;


    public ModelNotes(String title, String description, String owner) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        
    }

    public ModelNotes(String title, String owner) {
        this.title = title;
        this.owner = owner;
    }

    public ModelNotes() {

    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public Timestamp getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        TimeStamp = timeStamp;
    }
}
