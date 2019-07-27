package com.example.nativelife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class AddDestination extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private EditText country, city;
    private ImageView backBtn;
    //private EditText comment;
    private FloatingActionButton submit;
    //private Button submit;
    private MapView mapView;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;

    GoogleMap mMap;
    private static final int Request_User_Location_Code = 99;


    private Marker currentUserLocationMarker;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Bundle IntentBundle;
    private String userID;
    public HashMap<String, ArrayList<String>> countriesWithCities;
    public  ArrayList<String> lowerCaseCountryArray;
    public  ArrayList<String> lowerCaseCityArray;
    public  ArrayList<String> normalCountries;


    String country_text, city_text;
    public static double lati, longi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Countries");

        country = (EditText) findViewById(R.id.editTextCountryAdd);
        city = (EditText) findViewById(R.id.editTextCityAdd);
        submit = (FloatingActionButton) findViewById(R.id.buttonSubmit);
        mapView = (MapView) findViewById(R.id.mapView);

        backBtn = (ImageView) findViewById(R.id.backBtn);

        IntentBundle = getIntent().getExtras();
        userID = (String) IntentBundle.get("userID");

        countriesWithCities = LandingSearch.countryWithCites;
        lowerCaseCountryArray = LandingSearch.lowerCaseCountiesArraylist;
        lowerCaseCityArray = LandingSearch.lowerCaseCityArraylist;
        normalCountries = LandingSearch.normalCountries;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddDestination.this, LandingSearch.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                country_text = country.getText().toString();
                city_text = city.getText().toString();
                //String comment_text = comment.getText().toString();

                /* check all fields are filled out properly */
                if (country_text.isEmpty() || city_text.isEmpty()){

                    Toast.makeText(AddDestination.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();

                } else {

                    addCity();

                }
            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

    }

    /* Method that either creates a new Country and city, or adds to an city to an existing Country */
    private void addCity() {

        DatabaseReference cityRef;

        String lowercaseCountry = country_text.toLowerCase().replaceAll("\\s", "");
        String lowercaseCity = city_text.toLowerCase().replaceAll("\\s", "");


        if (lowerCaseCountryArray.contains(lowercaseCountry) ) {

            // need to check if city all ready exit as to not override info

            if (lowerCaseCityArray.contains(lowercaseCity)) {

                Toast.makeText(AddDestination.this, "Location already exists", Toast.LENGTH_SHORT).show();
                clickSubmitHome();

            } else {

                int indexNorm = lowerCaseCountryArray.indexOf(lowercaseCountry);
                String normCountry = normalCountries.get(indexNorm);

                cityRef = firebaseDatabase.getReference("Countries/" +  normCountry
                        + "/cities");
                City newCity = new City(city_text, normCountry);
                cityRef.child(city_text).setValue(newCity);

                clickSubmitConfirmation();
            }

        } else {
            CountryProfile countryProfile = new CountryProfile(country_text, city_text);
            databaseReference.child(country_text).setValue(countryProfile);
            clickSubmitConfirmation();

        }

    }

    private void clickSubmitHome(){

        Intent intent = new Intent(this, LandingSearch.class);
        intent.putExtra("userID", userID);
        startActivity(intent);

    }

    private void clickSubmitConfirmation(){

        Intent intent2 = new Intent(this, AddDesSubmit.class);
        intent2.putExtra("userID", userID);
        startActivity(intent2);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").draggable(true));
        mMap = map;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }

        if (mMap != null) {
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(AddDestination.this);
                    LatLng ll = marker.getPosition();
                    double lat = ll.latitude;
                    double lng = ll.longitude;

                    lati = lat;
                    longi = lng;

                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
//                    LatLng ll = marker.getPosition();
//                    String lat = String.valueOf(ll.latitude);
//                    String lng = String.valueOf(ll.longitude);
//                    marker.setTitle(lat);
//                    marker.setSnippet();
                }
            });
        }


    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }

            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this, "Permission denied....", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }




    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Geocoder gc = new Geocoder(AddDestination.this);
        double lat = latLng.latitude;
        double lng = latLng.longitude;

        lati = lat;
        longi = lng;

        List<Address> list = null;
        try {
            list = gc.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address add = list.get(0);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        markerOptions.title("current location: "+ add.getLocality() );
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.draggable(true);
        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        }
    }

    private void createCountryProfile() {

        String countrySelected = country.getText().toString().trim();
        String citySelected = city.getText().toString().trim();

    }
}
