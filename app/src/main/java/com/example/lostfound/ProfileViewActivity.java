package com.example.lostfound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileViewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView textViewUser, textViewPhone, textViewEmail, textViewSchool;
    private Button buttonBack;

    // Firebase auth object
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private String imageUrl;
    private String imageName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in that means current user will return null
        if (firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        Intent intent = getIntent();

        imageView = (ImageView) findViewById(R.id.imageView);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewSchool = (TextView) findViewById(R.id.textViewSchool);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        userId = intent.getStringExtra(LostPostViewActivity.LOSTPOSTINFORMATION_PROFILE);
        if (userId == null){
            userId = intent.getStringExtra(FoundPostViewActivity.FOUNDPOSTINFORMATION_PROFILE);
        }

        buttonBack.setOnClickListener(this);
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
                textViewUser.setText(userInformation.getName());
                textViewPhone.setText(userInformation.getPhoneNum());
                textViewEmail.setText(userInformation.getEmail());
                textViewSchool.setText(userInformation.getSchool());
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
            // stop current activity
            finish();
            //starting Lost activity
            startActivity(new Intent(this, LostActivity.class));
        }
    }
}
