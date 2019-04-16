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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private Button buttonBack;

    private String userId;
    private String messageUserId;
    private String messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        Intent intent = getIntent();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        messageUserId = intent.getStringExtra(ProfileViewActivity.LOSTPOSTINFORMATION_USERID);


        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(messageUserId)) {
                    messageId = dataSnapshot.child(messageUserId).getValue(String.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (messageId == null){
            String key = databaseReference.push().getKey();
            databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
            databaseReference.child(messageUserId).setValue(key);
            databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUserId + "/CHAT/");
            databaseReference.child(userId).setValue(key);
            databaseReference = FirebaseDatabase.getInstance().getReference("/");
            databaseReference.child("MESSAGES").setValue(key);
        }

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Populate to the chat
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
            startActivity(new Intent(this, LostActivity.class));
        }
    }
}
