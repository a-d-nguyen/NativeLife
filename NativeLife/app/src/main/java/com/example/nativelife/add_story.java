package com.example.nativelife;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class add_story extends AppCompatActivity {

    public static String username, country, city;
    EditText subject, description, location;
    String subjectT, descriptionT, locationT;
    Button submit;
    ToggleButton essentials, food, attractions, culture, safety, transportation;
    ImageView backBtn;
    ArrayList<ToggleButton> tags = new ArrayList<ToggleButton>();
    HashMap<String, String> tagsOn= new HashMap<>();
    HashMap<String, String> imgPathing;
    int index;

    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Button uploadBtn;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    private  static  final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";
    ArrayList<Uri> mArrayUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        mDatabase =FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("Story_Images/");
        mArrayUri = new ArrayList<Uri>();

        Intent intent = getIntent();
        Bundle intentExtras = intent.getExtras();
        username = (String) intentExtras.get("userName");
        country = (String) intentExtras.get("country");
        city = (String) intentExtras.get("city");
        imgPathing = new HashMap<String, String>();
        index = 0;

        databaseReference = mDatabase.getReference(country).child(city);

        subject = (EditText) findViewById(R.id.editText_subject);
        description = (EditText) findViewById(R.id.editText_description);
        location = (EditText) findViewById(R.id.editText_location);
        essentials = (ToggleButton) findViewById(R.id.toggleButton);
        food = (ToggleButton) findViewById(R.id.toggleButton2);
        attractions = (ToggleButton) findViewById(R.id.toggleButton3);
        culture = (ToggleButton) findViewById(R.id.toggleButton4);
        safety = (ToggleButton) findViewById(R.id.toggleButton5);
        transportation = (ToggleButton) findViewById(R.id.toggleButton6);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_story.this, LocationPage.class);
                intent.putExtra("userName", username);
                intent.putExtra("county", country);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        tags.add(essentials);
        tags.add(food);
        tags.add(attractions);
        tags.add(culture);
        tags.add(safety);
        tags.add(transportation);

        submit = (Button) findViewById(R.id.submit);

        textColorChange();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(add_story.this, LocationPage.class);
                subjectT = subject.getText().toString();
                descriptionT = description.getText().toString();
                locationT = location.getText().toString();

                for (ToggleButton x : tags){
                    if (x.isChecked()){
                        tagsOn.put(x.getText().toString(), "true");
                    }
                }

                if (subjectT.isEmpty() || descriptionT.isEmpty() || locationT.isEmpty()){
                    Toast.makeText(add_story.this, "Please make sure you have filled out all fields", Toast.LENGTH_SHORT).show();
                }else if (tagsOn.isEmpty()){
                    Toast.makeText(add_story.this, "Please check at least 1 tag", Toast.LENGTH_SHORT).show();
                } else {
                    uploadToStorage();
                    intent.putExtra("country", country);
                    intent.putExtra("city", city);
                    intent.putExtra("userName", username);
                    startActivity(intent);
                }
            }
        });

        uploadBtn = findViewById(R.id.uploadP);
        gvGallery = (GridView) findViewById(R.id.gv);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_PICK);
                intent.setPackage(GOOGLE_PHOTOS_PACKAGE_NAME);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null){
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery.getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadToStorage() {
        if (!(mArrayUri.isEmpty())){
            final Long nDate = System.currentTimeMillis();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            for (Uri x: mArrayUri) {
                String locID = country + "/" + city + "/";
                String imageID = username + "_" + UUID.randomUUID().toString() + ".jpeg";
                String fullImgID = "Story_Images/" + locID + imageID;
                final StorageReference ref = storageRef.child(locID + imageID);

                UploadTask uploadTask = ref.putFile(x);

                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(add_story.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(add_story.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            imgPathing.put(String.valueOf(index), downloadUri.toString());

                            index = index +1;

                            City temp = new City(city, country);
                            Story story = new Story(subjectT, descriptionT, locationT, imgPathing, username,
                                    nDate, tagsOn);

                            temp.addToCategory(story, city, country);
                        } else {
                            Toast.makeText(add_story.this, "Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void textColorChange() {
        essentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(essentials.getCurrentTextColor() == Color.WHITE)){
                    essentials.setTextColor(Color.WHITE);
                } else {
                    essentials.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(food.getCurrentTextColor() == Color.WHITE)){
                    food.setTextColor(Color.WHITE);
                } else {
                    food.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        attractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(attractions.getCurrentTextColor() == Color.WHITE)){
                    attractions.setTextColor(Color.WHITE);
                } else {
                    attractions.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        culture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(culture.getCurrentTextColor() == Color.WHITE)){
                    culture.setTextColor(Color.WHITE);
                } else {
                    culture.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(safety.getCurrentTextColor() == Color.WHITE)){
                    safety.setTextColor(Color.WHITE);
                } else {
                    safety.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });

        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(transportation.getCurrentTextColor() == Color.WHITE)){
                    transportation.setTextColor(Color.WHITE);
                } else {
                    transportation.setTextColor(Color.parseColor("#2D2D2D"));
                }
            }
        });
    }
}


