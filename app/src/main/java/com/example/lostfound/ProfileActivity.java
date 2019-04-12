package com.example.lostfound;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import android.util.Log;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Firebase auth object
    private FirebaseAuth firebaseAuth;

    // Firebase database reference
    private DatabaseReference databaseReference;

    //view objects
    private ImageView imageView;
    private TextView textViewUserEmail;
    private EditText editTextName, editTextSchool, editTextId, editTextPhoneNum;
    private Button buttonSave, buttonBack, mButtonChooseImage;

    private ProgressBar mProgressBar;
    private Uri mImageUri;

    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private String userId;
    private String imageUrl;
    private String imageName;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in that means current user will return null
        if (firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Initializing views
        imageView = (ImageView) findViewById(R.id.imageView);

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSchool = (EditText) findViewById(R.id.editTextSchool);
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextPhoneNum = (EditText) findViewById(R.id.editTextPhoneNum);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);

        mProgressBar = findViewById(R.id.progress_bar);

        // Getting current user and display logged in user name
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        textViewUserEmail.setText(user.getEmail());

        // Adding listener to button
        buttonBack.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("Profile");
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(final String path) {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Upload upload = new Upload("EMPTY", downloadUrl.toString());
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("/USERS/" + path);
                            mDatabaseRef.child("IMAGE").setValue(upload);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfilePic(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();
        uploadFile(userId);
    }

    private void saveUserInformation(){

        // Get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String name = editTextName.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String phoneNum = editTextPhoneNum.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name,school,id,user.getEmail(),phoneNum);

        // Create a node using unique id and set the value of that node to userInformation
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/");
        databaseReference.child(user.getUid().toString()).setValue(userInformation);
        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).fit().into(imageView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                editTextName.setText(userInformation.getName());
                editTextPhoneNum.setText(userInformation.getPhoneNum());
                editTextId.setText(userInformation.getId());
                editTextSchool.setText(userInformation.getSchool());
                imageUrl = dataSnapshot.child("IMAGE").child("imageUrl").getValue(String.class);
                imageName = dataSnapshot.child("IMAGE").child("name").getValue(String.class);
                Picasso.get().load(imageUrl).fit().into(imageView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonBack){
            //closing activity
            finish();
            //starting lost activity
            startActivity(new Intent(this, LostActivity.class));
        }
        else if (view == buttonSave){
            saveProfilePic();
            saveUserInformation();
        }
    }

}
