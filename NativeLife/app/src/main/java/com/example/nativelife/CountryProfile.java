package com.example.nativelife;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CountryProfile {

    public String country;
    public HashMap<String, City> cities;

    public CountryProfile(String country, String city) {
        this.country = country;

        // TODO: check if county exist
        this.cities = new HashMap<>();
        cities.put(city, new City(city,country));

    }

//    public CountryProfile(String country, String city, ArrayList<String> oldcities) {
//        this.country = country;
//
//        // TODO: check if county exist
//        this.cities = new HashMap<>();
//
//        cities.put(city, new City(city,country));
//
//    }





}
