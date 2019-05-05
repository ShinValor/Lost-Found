package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;
import android.widget.AdapterView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageViewActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ListView listView;

    private List<String> messageUsers;
    private List<String> messageIds;

    private String userId;

    public static final String POST_USER_ID = "com.example.lostfound.POST_USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_message_view);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        listView = (ListView) findViewById(R.id.listView);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        messageUsers = new ArrayList<>();
        messageIds = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageUsers.clear();
                messageIds.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    messageUsers.add(postSnapshot.getKey());
                    messageIds.add(postSnapshot.getValue(String.class));
                }
                MessageViewAdapter messageViewAdapter = new MessageViewAdapter(MessageViewActivity.this,messageIds);
                listView.setAdapter(messageViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String messageId = messageIds.get(i);
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(POST_USER_ID, messageId);
                startActivity(intent);
            }
        });


    }
}
