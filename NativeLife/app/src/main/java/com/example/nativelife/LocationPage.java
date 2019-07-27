package com.example.nativelife;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LocationPage extends AppCompatActivity {

    private static final String TAG = "Location Page.";
    private static final int NUM_COLUMNS = 2;

    public DatabaseReference mDatabase_init;
    public FirebaseDatabase mDatabase;
    public FirebaseStorage storage;
    private FusedLocationProviderClient fusedLocationClient;
    Location curLoc;


    HashMap<Long, ArrayList<String>> Data, DataSort;

    TextView title;
    String country, city, userName;
    FloatingActionButton fab;
    ToggleButton essentials, food, attractions, culture, safety, transportation;
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_page);

        storage = FirebaseStorage.getInstance();

        Data = new HashMap<Long, ArrayList<String>>();
        DataSort = new HashMap<Long, ArrayList<String>>();

        title = (TextView) findViewById(R.id.title);
        essentials = (ToggleButton) findViewById(R.id.essentials_button);
        food = (ToggleButton) findViewById(R.id.food_button);
        attractions = (ToggleButton) findViewById(R.id.attactions_button);
        culture = (ToggleButton) findViewById(R.id.culture_button);
        safety = (ToggleButton) findViewById(R.id.safety_button);
        transportation = (ToggleButton) findViewById(R.id.transportation_button);

        Intent intent = getIntent();
        Bundle intentExtras = intent.getExtras();
        userName = (String) intentExtras.get("userName");
        country = (String)intentExtras.get("country");
        city = (String) intentExtras.get("city");
        title.setText(city + ", " + country);
        fab = findViewById(R.id.add_a_story);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationPage.this, LandingSearch.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

        // show the initial page, with all the feeds
        initHashMap();

        essentials.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                DatabaseReference mDatabase_update;
                mDatabase_update = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city + "/essentials");

                if (ischecked) {
                    // the toggle is enable
                    addItem(mDatabase_update, "essentials");
                    essentials.setTextColor(Color.WHITE);
                } else {
                    // the toggle is disable
                    removeItem(mDatabase_update);
                    essentials.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        food.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                DatabaseReference mDatabase_update;
                mDatabase_update = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city + "/food");

                if (ischecked) {
                    // the toggle is enable
                    addItem(mDatabase_update, "food");
                    food.setTextColor(Color.WHITE);
                } else {
                    // the toggle is disable
                    removeItem(mDatabase_update);
                    food.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        safety.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                DatabaseReference mDatabase_update;
                mDatabase_update = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city + "/safety");

                if (ischecked) {
                    // the toggle is enable
                    addItem(mDatabase_update, "safety");
                    safety.setTextColor(Color.WHITE);
                } else {
                    // the toggle is disable
                    removeItem(mDatabase_update);
                    safety.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });


        transportation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                DatabaseReference mDatabase_update;
                mDatabase_update = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city + "/transportation");

                if (ischecked) {
                    // the toggle is enable
                    addItem(mDatabase_update, "transportation");
                    transportation.setTextColor(Color.WHITE);
                } else {
                    // the toggle is disable
                    removeItem(mDatabase_update);
                    transportation.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });


        attractions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                DatabaseReference mDatabase_update;
                mDatabase_update = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city + "/attractions");

                if (ischecked) {
                    // the toggle is enable
                    addItem(mDatabase_update, "attractions");
                    attractions.setTextColor(Color.WHITE);
                } else {
                    // the toggle is disable
                    removeItem(mDatabase_update);
                    attractions.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        culture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                DatabaseReference mDatabase_update;
                mDatabase_update = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city + "/culture");

                if (ischecked) {
                    // the toggle is enable
                    addItem(mDatabase_update, "culture");
                    culture.setTextColor(Color.WHITE);
                } else {
                    // the toggle is disable
                    removeItem(mDatabase_update);
                    culture.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(LocationPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference locDataRef = mDatabase.getReference("Countries/" + country + "/cities/" + city);

        Task<Location> loc = fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            curLoc = location;
                        }
                    }
                });

        locDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long lati = dataSnapshot.child("lati").getValue(Long.class);
                Long longi = dataSnapshot.child("longi").getValue(Long.class);
                final Location cityLoc = new Location(city);
                cityLoc.setLatitude(lati);
                cityLoc.setLongitude(longi);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final float distanceBtwn = curLoc.distanceTo(cityLoc);
                        if (distanceBtwn <= 160934) {
                            Intent intent = new Intent(LocationPage.this, add_story.class);
                            intent.putExtra("userName", userName);
                            intent.putExtra("country", country);
                            intent.putExtra("city", city);
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LocationPage.this);
                            builder.setCancelable(true);
                            builder.setTitle("You are not close enough to this location to post!");
                            builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addItem(DatabaseReference mDatabase_update, String cat) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Content...");
        progressDialog.show();

        final String Catogory = cat;
        mDatabase_update.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url, name;
                long date;

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //data.getValue(String.class)
                    name = data.getKey();
                    if (name.equals("-1") || DataSort.containsKey(name) || name.equals("test")) {

                    } else{
                        date = data.child("date").getValue(Long.class);
                        url = data.child("pictures/0").getValue(String.class);

                        ArrayList<String> val = new ArrayList<String>(
                                Arrays.asList(url, name, Catogory));
                        DataSort.put(date, val);
                    }
                }
                initRecyclerView(DataSort);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void removeItem(DatabaseReference mDatabase_update) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Content...");
        progressDialog.show();

        mDatabase_update.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long date;

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    date = data.child("date").getValue(Long.class);
                    if (DataSort.containsKey(date)) {
                        DataSort.remove(date);
                    }
                }
                initRecyclerView(DataSort);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initHashMap(){
        Log.d(TAG, "initHashMap: preparing hashmap.");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Content...");
        progressDialog.show();

        mDatabase_init = FirebaseDatabase.getInstance().getReference("Countries/" + country + "/cities/" + city);

        mDatabase_init.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> categories = new ArrayList<String>(
                        Arrays.asList("culture",
                                "food",
                                "essentials",
                                "attractions",
                                "safety",
                                "transportation"));

                for (int counter = 0; counter < categories.size(); counter++) {

                    String cat = categories.get(counter);

                    DataSnapshot data_cat = dataSnapshot.child(cat);

                    for (DataSnapshot data : data_cat.getChildren()) {
                        //data.getValue(String.class)
                        String url, name;
                        long date;

                        name = data.getKey();

                        if (name.equals("-1") || (Data.containsKey(name)) || name.equals("test")) {

                        } else {
                            date = data.child("date").getValue(Long.class);
                            url = data.child("pictures/0").getValue(String.class);
                            ArrayList<String> val = new ArrayList<String>(
                                    Arrays.asList(url, name, cat));
                            Data.put(date, val);
                        }
                    }
                }
                initRecyclerView(Data);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(HashMap<Long, ArrayList<String>> dataPick){
        Log.d(TAG, "initRecyclerView: initializing staggered recyclerview.");

        Set toSort = dataPick.keySet();
        List<Long> sorted = new ArrayList<Long>(toSort);
        Collections.sort(sorted);
        Collections.reverse(sorted);

        ArrayList<String> mImageUrls = new ArrayList<>();
        ArrayList<String> mNames = new ArrayList<>();
        ArrayList<String> mCat = new ArrayList<>();

        for (int counter = 0; counter < sorted.size(); counter++) {
            ArrayList<String> tmp;
            tmp = dataPick.get(sorted.get(counter));
            mImageUrls.add(tmp.get(0));
            mNames.add(tmp.get(1));
            mCat.add(tmp.get(2));
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLP);

        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls, mCat, country, city, userName);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }

}
