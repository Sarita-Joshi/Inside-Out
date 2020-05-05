package com.sarita.insideout;

import com.google.firebase.Timestamp;

public class ModelBooks {

    String title;
    String owner;
    Timestamp timestamp;


    public ModelBooks(String title, String owner, Timestamp timestamp) {

        this.title = title;
        this.owner = owner;
        this.timestamp = timestamp;
    }

    public ModelBooks() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ModelBooks{" +
                "title='" + title + '\'' +
                ", owner='" + owner + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}


