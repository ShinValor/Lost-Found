package com.example.lostfound.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lostfound.Adapters.MessageViewAdapter;
import com.example.lostfound.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

public class MessageViewActivity extends AppCompatActivity {

    // Declare variables

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ListView listView;

    private SearchView searchView;

    private List<String> messageUsers;
    private List<String> messageIds;

    private String userId;

    public static final String POST_USER_ID = "com.example.lostfound.postuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_message_view);

        firebaseAuth = FirebaseAuth.getInstance();

        // If user not login in, return to login activity
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        listView = (ListView) findViewById(R.id.listView);

        searchView = (SearchView) findViewById(R.id.searchView);

        userId = firebaseAuth.getCurrentUser().getUid();

        messageUsers = new ArrayList<>();
        messageIds = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //refreshList(newText);
                return false;
            }
        });

        // Populate all message history
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
                MessageViewAdapter messageViewAdapter = new MessageViewAdapter(MessageViewActivity.this,messageUsers);
                listView.setAdapter(messageViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Go to a particular message history to message with that user
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String messageUser = messageUsers.get(i);
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(POST_USER_ID, messageUser);
                startActivity(intent);
            }
        });


    }
}
