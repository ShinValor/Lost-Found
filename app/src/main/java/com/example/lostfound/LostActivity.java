package com.example.lostfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;

public class LostActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listViewLost;
    private FloatingActionButton buttonCreate;
    private Button buttonProfile, buttonFound, buttonLogout;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private List<PostInformation> listOfPosts;

    public static final String POSTINFORMATION_USER = "com.example.lostfound.postinformationuser",
                               POSTINFORMATION_TITLE = "com.example.lostfound.postinformationtitle",
                               POSTINFORMATION_DESCRIPTION = "com.example.lostfound.postinformationdescription",
                               POSTINFORMATION_PHONENUM = "com.example.lostfound.postinformationphonenum",
                               POSTINFORMATION_POSTID = "com.example.lostfound.postinformationpostid",
                               POSTINFORMATION_USERID = "com.example.lostfound.postinformationuserid",
                               POSTINFORMATION_PAGE = "com.example.lostfound.postinformationpage";

    private String route = "LOST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        buttonProfile = (Button) findViewById(R.id.buttonProfile);
        buttonFound = (Button) findViewById(R.id.buttonFound);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonCreate = (FloatingActionButton) findViewById(R.id.buttonCreate);
        listViewLost = (ListView) findViewById(R.id.listViewLost);

        listOfPosts = new ArrayList<>();

        buttonProfile.setOnClickListener(this);
        buttonFound.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        listViewLost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PostInformation postInformation = listOfPosts.get(i);

                Intent intent = new Intent(getApplicationContext(), LostPostViewActivity.class);

                intent.putExtra(POSTINFORMATION_USER, postInformation.getUser());
                intent.putExtra(POSTINFORMATION_TITLE, postInformation.getTitle());
                intent.putExtra(POSTINFORMATION_DESCRIPTION, postInformation.getDescription());
                intent.putExtra(POSTINFORMATION_PHONENUM, postInformation.getPhoneNum());
                intent.putExtra(POSTINFORMATION_POSTID, postInformation.getPostId());
                intent.putExtra(POSTINFORMATION_USERID, postInformation.getUserId());
                intent.putExtra(POSTINFORMATION_PAGE, route);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference("/LOST");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfPosts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PostInformation postInformation = postSnapshot.child("INFO").getValue(PostInformation.class);
                    listOfPosts.add(postInformation);
                }

                PostList lostAdapter = new PostList(LostActivity.this,listOfPosts);
                listViewLost.setAdapter(lostAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonProfile){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if (view == buttonFound){
            if (route == "LOST"){
                route = "FOUND";
            }
            else if (route == "FOUND"){
                route = "LOST";
            }
            databaseReference = FirebaseDatabase.getInstance().getReference("/" + route + "/");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listOfPosts.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PostInformation postInformation = postSnapshot.child("INFO").getValue(PostInformation.class);
                        listOfPosts.add(postInformation);
                    }

                    PostList foundAdapter = new PostList(LostActivity.this,listOfPosts);
                    listViewLost.setAdapter(foundAdapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if (view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if (view == buttonCreate){
            finish();
            Intent intent = new Intent(this, LostPostActivity.class);
            intent.putExtra(POSTINFORMATION_PAGE, route);
            startActivity(intent);
        }
    }
}