package com.example.lostfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.content.Intent;
import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import android.webkit.MimeTypeMap;
import android.content.ContentResolver;
import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import java.io.ByteArrayOutputStream;
import android.graphics.drawable.BitmapDrawable;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ImageView mImageView;
    private EditText editTextTitle, editTextDescription;
    private Button buttonPost, buttonCancel, mButtonChooseImage, buttonCamera;

    private ProgressBar mProgressBar;
    private Uri mImageUri;

    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private String route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();

        route = intent.getStringExtra(LostActivity.Post_PAGE);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mImageView = findViewById(R.id.image_view);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        buttonPost = (Button) findViewById(R.id.buttonPost);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCamera = (Button) this.findViewById(R.id.buttonCamera);
        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);

        mProgressBar = findViewById(R.id.progress_bar);

        buttonPost.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

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

        mStorageRef = FirebaseStorage.getInstance().getReference("/Post");
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
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    }, 500);
                    Toast.makeText(PostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    Upload upload = new Upload(downloadUrl.toString());
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("/" + route + "/" + path);
                    mDatabaseRef.child("IMAGE").setValue(upload);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(PostActivity.this, "Upload failed", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(PostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Upload upload = new Upload(downloadUrl.toString());
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("/" + route + "/" + path);
                mDatabaseRef.child("IMAGE").setValue(upload);

            }
        });
    }

    private void onPost(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();

        final String title = editTextTitle.getText().toString().trim();
        final String desc = editTextDescription.getText().toString().trim();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(userId)) {
                        String postUID = databaseReference.push().getKey();
                        User User = postSnapshot.child("INFO").getValue(User.class);
                        Post Post = new Post(User.getName(),title,desc,User.getPhoneNum(),userId,postUID);
                        databaseReference = FirebaseDatabase.getInstance().getReference("/" + route);
                        databaseReference.child(postUID).child("INFO").setValue(Post);
                        uploadFile(postUID);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        if (view == buttonPost){
            onPost();
            finish();
            startActivity(new Intent(this, LostActivity.class));
        }
        else if (view == buttonCancel){
            finish();
            startActivity(new Intent(this, LostActivity.class));
        }
    }
}
