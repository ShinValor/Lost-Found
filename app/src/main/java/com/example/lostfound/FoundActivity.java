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

    //we will use these constants later to pass information to another activity
    public static final String FOUNDPOSTINFORMATION_USER = "com.example.lostfound.foundpostinformationuser";
    public static final String FOUNDPOSTINFORMATION_TITLE = "com.example.lostfound.foundpostinformationtitle";
    public static final String FOUNDPOSTINFORMATION_DESCRIPTION = "com.example.lostfound.foundpostinformationdescription";
    public static final String FOUNDPOSTINFORMATION_PHONENUM = "com.example.lostfound.foundpostinformationphonenum";

    private ListView listViewFound;
    private Button buttonProfile, buttonLost, buttonCreate, buttonLogout;

    // Firebase auth object
    private FirebaseAuth firebaseAuth;

    // Firebase database reference
    private DatabaseReference databaseReference;

    List<FoundPostInformation> foundList;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND");
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
                FoundPostInformation foundPostInformation = foundList.get(i);

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), FoundPostViewActivity.class);

                //putting info to intent
                intent.putExtra(FOUNDPOSTINFORMATION_USER, foundPostInformation.getUser());
                intent.putExtra(FOUNDPOSTINFORMATION_TITLE, foundPostInformation.getTitle());
                intent.putExtra(FOUNDPOSTINFORMATION_DESCRIPTION, foundPostInformation.getDescription());
                intent.putExtra(FOUNDPOSTINFORMATION_PHONENUM, foundPostInformation.getPhoneNum());

                //starting the activity with intent
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //wipe entire foundList
                foundList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting post
                    FoundPostInformation foundPostInformation = postSnapshot.getValue(FoundPostInformation.class);
                    //add to list
                    foundList.add(foundPostInformation);
                }

                FoundList foundAdapter = new FoundList(FoundActivity.this,foundList);
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
        else if (view == buttonCreate){
            finish();
            //starting Found activity
            startActivity(new Intent(this, FoundPostActivity.class));
        }
        else if (view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
