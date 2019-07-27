package com.example.nativelife;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Story {

    public String subject, description, username, location;
    public HashMap<String, String> tags;
    public HashMap<String, String> pictures;

    public int hold;
    public long date;

    Story (){

    }

    Story (String subject, String description, String location, HashMap<String, String> pictures, String username,
           Long date, HashMap<String, String> tags){

        this.hold = 0;
        this.subject = subject;
        this.description = description;
        this.location = location;
        this.username = username;
        this.date = date;
        this.pictures = pictures;
        this.tags = tags;
    }

}
