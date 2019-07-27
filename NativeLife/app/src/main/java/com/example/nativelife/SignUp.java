package com.example.nativelife;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class SignUp extends AppCompatActivity  {

    // Things for uploading images
    private Boolean checkImageUpload = false;
    private ImageView userPhotoImageView;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    private Uri pickedImageUri;

    // All the info User inputs
    private EditText editTextFirstName, editTextLastName, editTextUserName;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private ImageView backBtn;

    private Button signUpButton;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private FirebaseDatabase firebaseDatabase;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("RegistrationInfo");

        progressDialog = new ProgressDialog(this);


        userPhotoImageView = (ImageView) findViewById(R.id.imageViewProfile);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.editTextConPassword);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });

        userPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        signUpButton = (Button) findViewById(R.id.buttonSignUp2);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
                //saveUserInformation();
            }
        });
    }

    /** This opens up the image folder on the phone, where user can pick there profile image. */
    private void openGallery() {

        checkImageUpload = true;
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);

    }

    // Have to do for version control
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(SignUp.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(SignUp.this, "Please accept required permissions", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(SignUp.this,
                        new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
    }


    /** After the user picks an image, this fills in the imageView */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // User has successfully picked an image
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data !=null) {

            pickedImageUri = data.getData();
            userPhotoImageView.setImageURI(pickedImageUri);
        }
    }

    private void openUserProfile() {
        Intent intent = new Intent(this, LandingSearch.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
        finish();
    }

    /** This method uploads the user's information into the database */
    private void saveUserInformation() {
        String firstName2 = editTextFirstName.getText().toString().trim();
        String lastName2 = editTextLastName.getText().toString().trim();
        String userName2 = editTextUserName.getText().toString().trim();
        String email2 = emailEditText.getText().toString().trim();
        String password2 = passwordEditText.getText().toString().trim();

        String splitEmail = email2.split("@")[0];

        UserRegistrationInfo userRegistrationInfo = new UserRegistrationInfo(firstName2, lastName2, userName2, email2, password2);

        //UserRegistrationInfo userRegistrationInfo = new UserRegistrationInfo(firstName2, lastName2, userName2, email2, password2, pickedImageUri);

        FirebaseUser user = firebaseAuth.getCurrentUser(); // make change
        userID = user.getUid();
        databaseReference.child(userID).setValue(userRegistrationInfo);

        //Toast.makeText(SignUp.this, "Information Saved...", Toast.LENGTH_SHORT).show();
    }

    /* This method is used to check Authentication of account */
    private void registerUser() {

        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        String checkValidEmail = email.split("@")[0];

        /* check all fields are filled out properly */
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ) {

            Toast.makeText(SignUp.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( (!(email.contains("@gmail.com")) && !(email.contains("@yahoo.com"))  && !(email.contains("@hotmail.com"))  && !(email.contains("@berkeley.edu"))
                && !(email.contains("@aol.com")) && !(email.contains(".edu")))) {
            Toast.makeText(SignUp.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 7) {
            Toast.makeText(SignUp.this, "Password must be at least 7 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkImageUpload) {
            Toast.makeText(SignUp.this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        /* Registration Part */
        if (password.equals(confirmPassword)) {
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            //saveUserInformation();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // user was able to register
                            if (task.isSuccessful()) {
                                //Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                createUserInfo(password, pickedImageUri, firebaseAuth.getCurrentUser());
                                //openUserProfile();
                            } else {
                                Toast.makeText(SignUp.this, "Email is already in use, click screen to enter another email", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        } else {
            Toast.makeText(SignUp.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            //return;
        }
    }

    /** This method uploads user's profile images */
    private void createUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
//        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());

        String userName2 = editTextUserName.getText().toString().trim();

        final StorageReference imageFilePath = mStorage.child( userName2 + "_" + pickedImageUri.getLastPathSegment());

        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded successfully
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            saveUserInformation();
                                            Toast.makeText(SignUp.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                            openUserProfile();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
}