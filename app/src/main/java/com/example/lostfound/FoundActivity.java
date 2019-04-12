package com.example.lostfound;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class FoundActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listViewFound;
    private Button buttonProfile, buttonLost, buttonCreate, buttonLogout;

    // Firebase auth object
    private FirebaseAuth firebaseAuth;

    // Firebase database reference
    private DatabaseReference databaseReference;

    private List<PostInformation> foundList;

    //we will use these constants later to pass information to another activity
    public static final String FOUNDPOSTINFORMATION_USER = "com.example.lostfound.foundpostinformationuser",
                               FOUNDPOSTINFORMATION_TITLE = "com.example.lostfound.foundpostinformationtitle",
                               FOUNDPOSTINFORMATION_DESCRIPTION = "com.example.lostfound.foundpostinformationdescription",
                               FOUNDPOSTINFORMATION_PHONENUM = "com.example.lostfound.foundpostinformationphonenum",
                               FOUNDPOSTINFORMATION_POSTID = "com.example.lostfound.foundpostinformationpostid",
                               FOUNDPOSTINFORMATION_USERID = "com.example.lostfound.foundpostinformationuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in that means current user will return null
        if (firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        buttonProfile = (Button) findViewById(R.id.buttonProfile);
        buttonLost = (Button) findViewById(R.id.buttonLost);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        listViewFound = (ListView) findViewById(R.id.listViewFound);

        foundList = new ArrayList<>();

        buttonProfile.setOnClickListener(this);
        buttonLost.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        //attaching listener to listview
        listViewFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                PostInformation postInformation = foundList.get(i);

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), FoundPostViewActivity.class);

                //putting info to intent
                intent.putExtra(FOUNDPOSTINFORMATION_USER, postInformation.getUser());
                intent.putExtra(FOUNDPOSTINFORMATION_TITLE, postInformation.getTitle());
                intent.putExtra(FOUNDPOSTINFORMATION_DESCRIPTION, postInformation.getDescription());
                intent.putExtra(FOUNDPOSTINFORMATION_PHONENUM, postInformation.getPhoneNum());
                intent.putExtra(FOUNDPOSTINFORMATION_POSTID, postInformation.getPostId());
                intent.putExtra(FOUNDPOSTINFORMATION_USERID, postInformation.getUserId());

                //starting the activity with intent
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //wipe entire foundList
                foundList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting post
                    PostInformation postInformation = postSnapshot.getValue(PostInformation.class);
                    //add to list
                    foundList.add(postInformation);
                }

                PostList foundAdapter = new PostList(FoundActivity.this,foundList);
                listViewFound.setAdapter(foundAdapter);

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
            //starting Profile activity
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if (view == buttonLost){
            finish();
            //starting Lost activity
            startActivity(new Intent(this, LostActivity.class));
        }
        else if (view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if (view == buttonCreate){
            finish();
            //starting Found activity
            startActivity(new Intent(this, FoundPostActivity.class));
        }
    }
}
