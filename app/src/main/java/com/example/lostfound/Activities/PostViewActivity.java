package com.example.lostfound.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostfound.Classes.GMailSender;
import com.example.lostfound.Classes.SecurityQuestions;
import com.example.lostfound.Fragments.LostFragment;
import com.example.lostfound.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PostViewActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare my variables

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private StorageTask uploadTask;
    private StorageReference storageRef;

    private ImageView imageViewPicture, imageViewProfile, imageViewCard, imageViewClose;
    private TextView textViewUser;
    private TextInputEditText textViewTitle, textViewDescription, editTextQuestion1, editTextQuestion2, editTextQuestion3;
    private Button buttonCall, buttonMessage, buttonTrack, buttonSubmit, buttonCamera;

    private SignaturePad signaturePad;
    private Dialog myDialog;

    private Context context = this;
    private Intent intent;
    private Uri imageUri;

    private String userId, userPostId, userPostEmail, postId, route, imageUrl;

    public static final String POST_PROFILE = "com.example.lostfound.lostpostprofile",
                               POST_USER_ID = "com.example.lostfound.postuserid",
                               POST_USER_EMAIL = "com.example.lostfound.postuseremail";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    // Send email to notify the user that he/she has the item
    void addNotification(final String email){
        // Create a new thread and send email
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender("lostfoundee32f@gmail.com","A24518190d");
                    //sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");
                    sender.sendMail(
                            "Found Item", "You have an item that someone lost.","lostfoundee32f@gmail.com",email);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    // Pop up for security question
    public void ShowPopup(View view) {
        myDialog.setContentView(R.layout.pop_up);

        // Initialize
        imageViewClose = (ImageView) myDialog.findViewById(R.id.imageViewClose);
        imageViewCard = (ImageView) myDialog.findViewById(R.id.imageViewCard);

        editTextQuestion1 = (TextInputEditText) myDialog.findViewById(R.id.editTextQuestion1);
        editTextQuestion2 = (TextInputEditText) myDialog.findViewById(R.id.editTextQuestion2);
        editTextQuestion3 = (TextInputEditText) myDialog.findViewById(R.id.editTextQuestion3);

        buttonSubmit = (Button) myDialog.findViewById(R.id.buttonSubmit);
        buttonCamera = (Button) myDialog.findViewById(R.id.buttonCamera);

        // Close popup
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        // Open up camera
        buttonCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        // Submit to the popup question
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Camera

                String name = editTextQuestion1.getText().toString().trim();
                String school = editTextQuestion2.getText().toString().trim();
                String id = editTextQuestion3.getText().toString().trim();

                signaturePad = (SignaturePad) myDialog.findViewById(R.id.signature_pad);

                // Security questions
                final SecurityQuestions security = new SecurityQuestions(name,school,id, postId);

                // Listen to the user signature
                signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                    @Override
                    public void onStartSigning() {
                        //Event triggered when the pad is touched
                    }

                    @Override
                    public void onSigned() {
                        //Event triggered when the pad is signed
                        Bitmap bitmap = signaturePad.getSignatureBitmap();
                    }

                    @Override
                    public void onClear() {
                        //Event triggered when the pad is cleared
                    }
                });

                // When submitted keep track of the transaction
                databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Send user email to notify
                        addNotification(userPostEmail);
                        myDialog.dismiss();
                        databaseReference.child("TRACK").child(postId).setValue(security);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    // Open up photo gallery
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Get file extension
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Get permission for camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
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

    // Get permission for camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).resize(300,150).into(imageViewCard);
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageViewCard.setImageBitmap(photo);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_post_view);

        firebaseAuth = FirebaseAuth.getInstance();

        // If user not login in, return to login activity
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        intent = getIntent();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize
        imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);

        textViewTitle = (TextInputEditText) findViewById(R.id.textViewTitle);
        textViewDescription = (TextInputEditText) findViewById(R.id.textViewDescription);
        textViewUser = (TextView) findViewById(R.id.textViewUser);

        buttonCall = (Button) findViewById(R.id.buttonCall);
        buttonMessage = (Button) findViewById(R.id.buttonMessage);
        buttonTrack = (Button) findViewById(R.id.buttonTrack);

        myDialog = new Dialog(this);

        // Get intent passed value
        textViewUser.setText(intent.getStringExtra(LostFragment.POST_USER));
        textViewTitle.setText(intent.getStringExtra(LostFragment.POST_TITLE));
        textViewDescription.setText(intent.getStringExtra(LostFragment.POST_DESCRIPTION));

        userId = intent.getStringExtra(LostFragment.POST_USER_ID);
        userPostId = intent.getStringExtra(LostFragment.POST_USER_ID);
        userPostEmail = intent.getStringExtra(LostFragment.POST_USER_EMAIL);
        postId = intent.getStringExtra(LostFragment.POST_ID);
        route = intent.getStringExtra(LostFragment.POST_ROUTE);

        // Disable writing textView
        textViewTitle.setEnabled(false);
        textViewDescription.setEnabled(false);

        // Set listeners
        textViewUser.setOnClickListener(this);
        buttonMessage.setOnClickListener(this);
        buttonTrack.setOnClickListener(this);
        buttonCall.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set item image into imageViewPicture
        databaseReference = FirebaseDatabase.getInstance().getReference("/" + route + "/" + postId + "/IMAGE");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                Picasso.get().load(imageUrl).resize(300,300).into(imageViewPicture);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Set user profile image into imageViewProfile
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/IMAGE");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                Picasso.get().load(imageUrl).resize(120,120).into(imageViewProfile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonMessage){
            // Message the user
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)){
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(POST_USER_ID,userId);
                intent.putExtra(POST_USER_EMAIL, userPostEmail);
                startActivity(intent);
            }
            else{
                Toast.makeText(context,"You can not message yourself.",Toast.LENGTH_LONG).show();
            }
        }
        else if (view == buttonTrack){
            // Track user, popup will show up for verification
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)){
                ShowPopup(view);
            }
            else{
                Toast.makeText(context,"You can not track yourself.",Toast.LENGTH_LONG).show();
            }
        }
        else if (view == buttonCall){
            // Open phone call activity
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)){
                String phoneNum = "+" + intent.getStringExtra(LostFragment.POST_PHONE_NUMBER);
                Intent intent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                intent.putExtra(POST_PROFILE,userId);
                startActivity(intent);
            }
            else{
                Toast.makeText(context,"You can not call yourself.",Toast.LENGTH_LONG).show();
            }
        }
        else if (view == textViewUser){
            // Start profileview activity
            Intent intent = new Intent(getApplicationContext(), ProfileViewActivity.class);
            intent.putExtra(POST_PROFILE,userId);
            startActivity(intent);
        }
    }
}
