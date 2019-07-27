package com.example.nativelife;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LandingSearch extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewCountry;
    private AutoCompleteTextView autoCompleteTextViewCity;


    private TextView addDestination;
    private Button jumpLocation;

    String countrySelected;
    String citySelected;
    ArrayList<String> citesToSelectFrom;


    // add Firebase Database stuff
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;

    public static HashMap<String, ArrayList<String>> countryWithCites = new HashMap<>();
    private ArrayList<String> countiesArraylist = new ArrayList<>();
    public static ArrayList<String> normalCountries = new ArrayList<>();
    public static ArrayList<String> lowerCaseCountiesArraylist = new ArrayList<>();
    public static ArrayList<String> lowerCaseCityArraylist = new ArrayList<>();


    private String userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_search);


        Intent intent = getIntent();
        Bundle signInOrUpIntentExtras = intent.getExtras();
        userID = (String) signInOrUpIntentExtras.get("userID");

        databaseReference = FirebaseDatabase.getInstance().getReference("RegistrationInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                getUserNameFromID(userID, dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getCountiesAndCites();

        autoCompleteTextViewCountry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewCountry);
        ArrayAdapter<String> adaperCountry = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countiesArraylist);
        autoCompleteTextViewCountry.setAdapter(adaperCountry);


        autoCompleteTextViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                countrySelected = autoCompleteTextViewCountry.getText().toString();
                citesToSelectFrom = countryWithCites.get(countrySelected);

                autoCompleteTextViewCity = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewCity);
                ArrayAdapter<String>  adaperCities = new ArrayAdapter<String>(LandingSearch.this, android.R.layout.simple_list_item_1, citesToSelectFrom);
                autoCompleteTextViewCity.setAdapter(adaperCities);

            }
        });

        addDestination = (TextView) findViewById(R.id.textViewAddDestination);
        jumpLocation = (Button) findViewById(R.id.buttonSubmit);

        addDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAddDestination();

            }
        });

        jumpLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (TextUtils.isEmpty(countrySelected) ) {
//                    Toast.makeText(LandingSearch.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
//
//                    return;
//                }
//
//                citySelected = autoCompleteTextViewCity.getText().toString();
//
//                if (TextUtils.isEmpty(citySelected)  ) {
//                    Toast.makeText(LandingSearch.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
//
//                    return;
//                }

                // can't advance unless they pick something from the available list
                if (!(countryWithCites.containsKey(countrySelected))) {
                    Toast.makeText(LandingSearch.this, "Please pick from available locations", Toast.LENGTH_SHORT).show();
                    return;
                }

                citySelected = autoCompleteTextViewCity.getText().toString();

                if (!(citesToSelectFrom.contains(citySelected))) {
                    Toast.makeText(LandingSearch.this, "Please pick from available locations", Toast.LENGTH_SHORT).show();
                    return;
                }

                openLocationPage();
            }
        });
    }

    private void getCountiesAndCites() {

        databaseReference2 = FirebaseDatabase.getInstance().getReference("Countries");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String currentCountry = ds.getKey();

                    HashMap<String, Object> userHashMap = (HashMap<String, Object>) ds.getValue();
                    HashMap<String, Object> citesInCountry = (HashMap<String, Object>) userHashMap.get("cities");

                    ArrayList<String> currCites = new ArrayList<String>();

                    for (String key : citesInCountry.keySet()) {
                        currCites.add(key);
                        lowerCaseCityArraylist.add(key.toLowerCase().replaceAll("\\s", ""));
                    }

                    normalCountries.add(currentCountry);
                    lowerCaseCountiesArraylist.add(currentCountry.toLowerCase().replaceAll("\\s", ""));
                    countiesArraylist.add(currentCountry);
                    countryWithCites.put(currentCountry, currCites);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openLocationPage(){
        citySelected = autoCompleteTextViewCity.getText().toString();

//        Toast.makeText(LandingSearch.this, userName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LocationPage.class);
        intent.putExtra("country", countrySelected);
        intent.putExtra("city", citySelected);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void openAddDestination(){
        Intent intent2 = new Intent(this, AddDestination.class);
        intent2.putExtra("userID", userID);
        startActivity(intent2);
    }

    private void getUserNameFromID(String userID, DataSnapshot dataSnapshot) {

        // https://www.youtube.com/watch?v=2duc77R4Hqw
        String user_nameRec = "TempName";

        for(DataSnapshot ds : dataSnapshot.getChildren()) {

            String tempUserID = userID;
            String keyOfID = ds.getKey();

            HashMap<String, Object> userHashMap = (HashMap<String, Object>) ds.getValue();

            if (userID.equals(ds.getKey())) {

                user_nameRec = (String) userHashMap.get("userNameReg");
                userName = (String) userHashMap.get("userNameReg");
                break;

            }
//            Toast.makeText(LandingSearch.this, user_nameRec, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
