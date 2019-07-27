package com.example.nativelife;

import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class
City {

    public HashMap<String, Story> essentials, food, attractions, culture, safety, transportation;
    public String cityName, countryName;
    public ArrayList<Integer> indexes;

    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    public double lati, longi;

    City (String city, String country){
        this.cityName = city;
        this.countryName = country;

        this.mDatabase =FirebaseDatabase.getInstance();
//        databaseReference = mDatabase.getReference("Countries").child(this.countryName).child(this.cityName);
        //databaseReference = mDatabase.getReference("Countries" + countryName + "/cities/" + cityName);

        lati = AddDestination.lati;
        longi = AddDestination.longi;



        essentials = new HashMap<>();
        essentials.put("test", new Story());
        food = new HashMap<>();
        food.put("-1", new Story());
        attractions = new HashMap<>();
        attractions.put("-1", new Story());
        culture = new HashMap<>();
        culture.put("-1", new Story());
        safety = new HashMap<>();
        safety.put("-1", new Story());
        transportation = new HashMap<>();
        transportation.put("-1",new Story());

    }

    public void set_story_indexes(ArrayList<Integer> numStories){
        indexes = numStories;
    }

    public void addToCategory(Story userStory, String city, String country) {
        HashMap<String, String> tags = userStory.tags;
        DatabaseReference tagRef;

        if (tags.containsKey("Essentials")) {
            tagRef = mDatabase.getReference("Countries/" + countryName + "/cities/" + cityName + "/essentials");
            tagRef.child(userStory.subject).setValue(userStory);
        }
        if (tags.containsKey("Food")) {
            tagRef = mDatabase.getReference("Countries/" + countryName + "/cities/" + cityName + "/food");
            tagRef.child(userStory.subject).setValue(userStory);
        }
        if (tags.containsKey("Attractions")) {
            tagRef = mDatabase.getReference("Countries/" + countryName + "/cities/" + cityName + "/attractions");
            tagRef.child(userStory.subject).setValue(userStory);
        }
        if (tags.containsKey("Culture")) {
            tagRef = mDatabase.getReference("Countries/" + countryName + "/cities/" + cityName + "/culture");
            tagRef.child(userStory.subject).setValue(userStory);
        }
        if (tags.containsKey("Safety")) {
            tagRef = mDatabase.getReference("Countries/" + countryName + "/cities/" + cityName + "/safety");
            tagRef.child(userStory.subject).setValue(userStory);
        }
        if (tags.containsKey("Transportation")) {
            tagRef = mDatabase.getReference("Countries/" + countryName + "/cities/" + cityName + "/transportation");
            tagRef.child(userStory.subject).setValue(userStory);
        }
    }
}
