package com.example.lostfound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class PostViewActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ImageView imageViewPicture, imageViewProfile;
    private TextInputEditText textViewTitle, textViewDescription;
    private TextView textViewUser;
    private Button buttonCall, buttonMessage, buttonTrack;

    Dialog myDialog;

    private String userId, userPostId, postId, route, imageUrl;

    private Context context = this;
    private Intent intent;

    public static final String POST_PROFILE = "com.example.lostfound.lostpostprofile",
                               POST_USER_ID = "com.example.lostfound.postuserid";

    public void ShowPopup(View view) {
        myDialog.setContentView(R.layout.layout_popup);

        TextView textViewClose;
        final TextInputEditText editTextQuestion1, editTextQuestion2, editTextQuestion3;
        Button buttonSubmit;

        textViewClose =(TextView) myDialog.findViewById(R.id.textViewClose);
        editTextQuestion1 = (TextInputEditText) myDialog.findViewById(R.id.editTextQuestion1);
        editTextQuestion2 = (TextInputEditText) myDialog.findViewById(R.id.editTextQuestion2);
        editTextQuestion3 = (TextInputEditText) myDialog.findViewById(R.id.editTextQuestion3);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        textViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp1 = editTextQuestion1.getText().toString().trim();
                String temp2 = editTextQuestion2.getText().toString().trim();
                String temp3 = editTextQuestion3.getText().toString().trim();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    void addNotification(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender("lostfoundee32f@gmail.com","A24518190d");
                    //sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");
                    sender.sendMail("Found Item", "You have an item that someone lost.","lostfoundee32f@gmail.com","frodo1642@gmail.com");
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_post_view);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        intent = getIntent();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);

        textViewTitle = (TextInputEditText) findViewById(R.id.textViewTitle);
        textViewDescription = (TextInputEditText) findViewById(R.id.textViewDescription);
        textViewUser = (TextView) findViewById(R.id.textViewUser);

        buttonCall = (Button) findViewById(R.id.buttonCall);
        buttonMessage = (Button) findViewById(R.id.buttonMessage);
        buttonTrack = (Button) findViewById(R.id.buttonTrack);

        myDialog = new Dialog(this);

        textViewUser.setText(intent.getStringExtra(LostFragment.POST_USER));
        textViewTitle.setText(intent.getStringExtra(LostFragment.POST_TITLE));
        textViewDescription.setText(intent.getStringExtra(LostFragment.POST_DESCRIPTION));

        userId = intent.getStringExtra(LostFragment.POST_USER_ID);
        userPostId = intent.getStringExtra(LostFragment.POST_USER_ID);
        postId = intent.getStringExtra(LostFragment.POST_ID);
        route = intent.getStringExtra(LostFragment.POST_ROUTE);

        textViewTitle.setEnabled(false);
        textViewDescription.setEnabled(false);

        textViewUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                intent.putExtra(POST_PROFILE,userPostId);
                startActivity(intent);
            }
        });

        buttonMessage.setOnClickListener(this);
        buttonTrack.setOnClickListener(this);
        buttonCall.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference("/" + route + "/" + postId + "/IMAGE");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                Picasso.get().load(imageUrl).resize(300,300).into(imageViewPicture);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/IMAGE");
        databaseReference.addValueEventListener(new ValueEventListener() {
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
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)){
                //finish();
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(POST_USER_ID,userId);
                startActivity(intent);
            }
            else{
                Toast.makeText(context,"You can not message yourself.",Toast.LENGTH_LONG).show();
            }
        }
        else if (view == buttonTrack){
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)){
                addNotification();
                ShowPopup(view);
                //startActivity(new Intent(this, MapActivity.class));
            }
            else{
                Toast.makeText(context,"You can not track yourself.",Toast.LENGTH_LONG).show();
            }
        }
        else if (view == buttonCall){
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)){
                String phoneNum = "+" + intent.getStringExtra(LostFragment.POST_PHONE_NUMBER);
                Intent intent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                intent.putExtra(POST_PROFILE,userPostId);
                startActivity(intent);
            }
            else{
                Toast.makeText(context,"You can not call yourself.",Toast.LENGTH_LONG).show();
            }
        }
    }
}
