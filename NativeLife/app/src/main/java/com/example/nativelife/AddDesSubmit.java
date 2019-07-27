package com.example.nativelife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddDesSubmit extends AppCompatActivity {

    private Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_des_submit);

        home = (Button) findViewById(R.id.buttonBackHome);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backHome();
            }
        });
    }

    private void backHome(){

        Bundle IntentBundle = getIntent().getExtras();
        String userID = (String) IntentBundle.get("userID");

        Intent intent = new Intent(this, LandingSearch.class);
        intent.putExtra("userID", userID);
        startActivity(intent);

    }
}
