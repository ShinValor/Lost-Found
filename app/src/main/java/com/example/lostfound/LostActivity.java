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
    private Button buttonProfile, buttonScreen, buttonLogout;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private List<Post> listOfPosts;

    public static final String Post_USER = "com.example.lostfound.Postuser",
                               Post_TITLE = "com.example.lostfound.Posttitle",
                               Post_DESCRIPTION = "com.example.lostfound.Postdescription",
                               Post_PHONENUM = "com.example.lostfound.Postphonenum",
                               Post_POSTID = "com.example.lostfound.Postpostid",
                               Post_USERID = "com.example.lostfound.Postuserid",
                               Post_PAGE = "com.example.lostfound.Postpage";

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
        buttonScreen = (Button) findViewById(R.id.buttonScreen);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonCreate = (FloatingActionButton) findViewById(R.id.buttonCreate);
        listViewLost = (ListView) findViewById(R.id.listViewLost);

        buttonScreen.setText(route);

        listOfPosts = new ArrayList<>();

        buttonProfile.setOnClickListener(this);
        buttonScreen.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        listViewLost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Post Post = listOfPosts.get(i);

                Intent intent = new Intent(getApplicationContext(), PostViewActivity.class);

                intent.putExtra(Post_USER, Post.getUser());
                intent.putExtra(Post_TITLE, Post.getTitle());
                intent.putExtra(Post_DESCRIPTION, Post.getDescription());
                intent.putExtra(Post_PHONENUM, Post.getPhoneNum());
                intent.putExtra(Post_POSTID, Post.getPostId());
                intent.putExtra(Post_USERID, Post.getUserId());
                intent.putExtra(Post_PAGE, route);

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
                    Post Post = postSnapshot.child("INFO").getValue(Post.class);
                    listOfPosts.add(Post);
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
        else if (view == buttonScreen){
            if (route == "LOST"){
                route = "FOUND";
                buttonScreen.setText(route);
            }
            else if (route == "FOUND"){
                route = "LOST";
                buttonScreen.setText(route);
            }
            databaseReference = FirebaseDatabase.getInstance().getReference("/" + route + "/");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listOfPosts.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post Post = postSnapshot.child("INFO").getValue(Post.class);
                        listOfPosts.add(Post);
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
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra(Post_PAGE, route);
            startActivity(intent);
        }
    }
}