package com.example.lostfound;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import java.io.ByteArrayOutputStream;
import android.graphics.drawable.BitmapDrawable;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ImageView mImageView;
    private TextView textViewUserEmail;
    private TextInputEditText editTextName, editTextSchool, editTextId, editTextPhoneNum;
    private Button buttonSave, buttonBack, mButtonChooseImage, buttonCamera;

    private ProgressBar mProgressBar;
    private Uri mImageUri;

    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private String userId;
    private String imageUrl;
    private String imageName;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mImageView = (ImageView) findViewById(R.id.imageView);

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextSchool = (TextInputEditText) findViewById(R.id.editTextSchool);
        editTextId = (TextInputEditText) findViewById(R.id.editTextId);
        editTextPhoneNum = (TextInputEditText) findViewById(R.id.editTextPhoneNum);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonCamera = (Button) this.findViewById(R.id.buttonCamera);
        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);

        mProgressBar = findViewById(R.id.progress_bar);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        textViewUserEmail.setText(user.getEmail());

        buttonBack.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("/Profile");

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Do whatever you need with your data (retrieved only once)
                UserInformation userInformation = dataSnapshot.child("INFO").getValue(UserInformation.class);
                if (userInformation != null){
                    editTextName.setText(userInformation.getName());
                    editTextPhoneNum.setText(userInformation.getPhoneNum());
                    editTextId.setText(userInformation.getId());
                    editTextSchool.setText(userInformation.getSchool());
                    imageUrl = dataSnapshot.child("IMAGE").child("imageUrl").getValue(String.class);
                    imageName = dataSnapshot.child("IMAGE").child("name").getValue(String.class);
                    Picasso.get().load(imageUrl).fit().into(mImageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                            Upload upload = new Upload(downloadUrl.toString());
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
            cameraUpload(path);
        }
    }

    private void cameraUpload(final String path) {
        // Get the data from an ImageView as bytes
        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ProfileActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Upload upload = new Upload(downloadUrl.toString());
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("/USERS/" + path);
                mDatabaseRef.child("IMAGE").setValue(upload);
            }
        });
    }

    private void saveProfilePic(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();
        uploadFile(userId);
    }

    private void saveUserInformation(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        String name = editTextName.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String phoneNum = editTextPhoneNum.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name,school,id,user.getEmail(),phoneNum);

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/");
        databaseReference.child(user.getUid().toString()).child("INFO").setValue(userInformation);
        databaseReference.child(user.getUid()).child("CHAT").setValue(false);
        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).fit().into(mImageView);
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(photo);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonBack){
            finish();
            startActivity(new Intent(this, LostActivity.class));
        }
        else if (view == buttonSave){
            saveProfilePic();
            saveUserInformation();
        }
    }

}
