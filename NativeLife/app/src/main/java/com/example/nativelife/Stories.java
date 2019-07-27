package com.example.nativelife;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Stories extends AppCompatActivity {
    TextView usernameV, subjectV, descriptionV, locationV, tagsV, dateV;
    ImageView backBtn;
    String username, subject, description, location, category, country, city, tagList, postUser;
    Long date;

    ViewFlipper v_flipper;

    FirebaseDatabase mDatabase;
    DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        usernameV = (TextView) findViewById(R.id.textEditUserName);
        subjectV = (TextView) findViewById(R.id.textEditPostName);
        descriptionV = (TextView) findViewById(R.id.description);
        locationV = (TextView) findViewById(R.id.title);
        tagsV = (TextView) findViewById(R.id.tags);
        dateV = (TextView) findViewById(R.id.textEditDate);
        v_flipper = findViewById(R.id.flipper);

        Intent intent = getIntent();
        Bundle intentExtras = intent.getExtras();
        username = (String) intentExtras.get("userName");
        subject = (String) intentExtras.get("subject");
        country = (String) intentExtras.get("country");
        city = (String) intentExtras.get("city");
        category = (String) intentExtras.get("category");
        tagList = "";

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Stories.this, LocationPage.class);
                intent.putExtra("userName", username);
                intent.putExtra("country", country);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance();
        dataRef = mDatabase.getReference("Countries/" + country + "/cities/" + city + "/" + category + "/" + subject);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postUser = dataSnapshot.child("username").getValue(String.class);
                description = dataSnapshot.child("description").getValue(String.class);
                location = dataSnapshot.child("location").getValue(String.class);
                date = dataSnapshot.child("date").getValue(Long.class);
                DataSnapshot temp = dataSnapshot.child("tags");
                for (DataSnapshot data2: temp.getChildren()) {
                    tagList = tagList + data2.getKey() + ", ";
                }
//                usernameV.setText("by " + "Anonymous");
                usernameV.setText("by " + postUser);
                subjectV.setText(subject);
                descriptionV.setText(description);
                locationV.setText(location);
                tagsV.setText(tagList.substring(0, tagList.length() -2));

                DateFormat dF = new SimpleDateFormat("yyyy-MM-dd");
                dateV.setText(dF.format(date));

                String url;
                Integer numPhotos = 0;
                DataSnapshot temp2 = dataSnapshot.child("pictures");
                for (DataSnapshot data3: temp2.getChildren()){
                    url = data3.getValue(String.class);
                    System.out.println(url +  " =========URLS");
                    flipperImages(data3.getValue(String.class));
                    numPhotos += 1;
                }

                if (numPhotos > 1){
                    v_flipper.setAutoStart(true);
                    v_flipper.startFlipping();
                };

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void flipperImages(String dUrl){
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);

        Glide.with(this /* context */)
                .load(dUrl)
                .apply(requestOptions)
                .into(imageView);



        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);

        v_flipper.setInAnimation(this, R.anim.slide_in_right);
        v_flipper.setOutAnimation(this, R.anim.slide_out_left);
    }
}
