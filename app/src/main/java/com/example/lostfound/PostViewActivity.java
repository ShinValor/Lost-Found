package com.example.lostfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ImageView imageViewPicture, imageViewProfile;

    private TextInputEditText textViewTitle, textViewDescription;
    private TextView textViewUser;
    private Button buttonBack, buttonCall, buttonMessage;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private String userId;
    private String postId;
    private String imageUrl;
    private String route;

    public static final String POST_PROFILE = "com.example.lostfound.lostpostprofile",
                               POST_USER_ID = "com.example.lostfound.POST_USER_ID";

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

        Intent intent = getIntent();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        textViewTitle = (TextInputEditText) findViewById(R.id.textViewTitle);
        textViewDescription = (TextInputEditText) findViewById(R.id.textViewDescription);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonCall = (Button) findViewById(R.id.buttonCall);
        buttonMessage = (Button) findViewById(R.id.buttonMessage);

        textViewUser.setText(intent.getStringExtra(LostFragment.POST_USER));
        textViewTitle.setText(intent.getStringExtra(LostFragment.POST_TITLE));
        textViewDescription.setText(intent.getStringExtra(LostFragment.POST_DESCRIPTION));

        userId = intent.getStringExtra(LostFragment.POST_USER_ID);
        postId = intent.getStringExtra(LostFragment.POST_ID);
        route = intent.getStringExtra(LostFragment.POST_ROUTE);

        textViewTitle.setEnabled(false);
        textViewDescription.setEnabled(false);

        buttonBack.setOnClickListener(this);

        final String userPostId = intent.getStringExtra(LostFragment.POST_USER_ID);
        textViewUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                intent.putExtra(POST_PROFILE,userPostId);
                startActivity(intent);
            }
        });

        final String phoneNum = "+" + intent.getStringExtra(LostFragment.POST_PHONE_NUMBER);
        buttonCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNum, null));
                startActivity(intent);
            }
        });

        buttonMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(POST_USER_ID,userId);
                startActivity(intent);
            }
        });
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
        if (view == buttonBack){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
