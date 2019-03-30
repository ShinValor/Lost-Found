package com.example.lostfound;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
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

public class LostActivity extends AppCompatActivity implements View.OnClickListener {

    //we will use these constants later to pass information to another activity
    public static final String LOSTPOSTINFORMATION_USER = "com.example.lostfound.lostpostinformationuser",
                               LOSTPOSTINFORMATION_TITLE = "com.example.lostfound.lostpostinformationtitle",
                               LOSTPOSTINFORMATION_DESCRIPTION = "com.example.lostfound.lostpostinformationdescription",
                               LOSTPOSTINFORMATION_PHONENUM = "com.example.lostfound.lostpostinformationphonenum";

    private ListView listViewLost;
    private Button buttonProfile, buttonFound, buttonCreate, buttonLogout;

    // Firebase auth object
    private FirebaseAuth firebaseAuth;

    // Firebase database reference
    private DatabaseReference databaseReference;

    List<LostPostInformation> lostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

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
        buttonFound = (Button) findViewById(R.id.buttonFound);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        listViewLost = (ListView) findViewById(R.id.listViewLost);

        databaseReference = FirebaseDatabase.getInstance().getReference("/LOST");
        lostList = new ArrayList<>();

        buttonProfile.setOnClickListener(this);
        buttonFound.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        //attaching listener to listview
        listViewLost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                LostPostInformation lostPostInformation = lostList.get(i);

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), LostPostViewActivity.class);

                //putting info to intent
                intent.putExtra(LOSTPOSTINFORMATION_USER, lostPostInformation.getUser());
                intent.putExtra(LOSTPOSTINFORMATION_TITLE, lostPostInformation.getTitle());
                intent.putExtra(LOSTPOSTINFORMATION_DESCRIPTION, lostPostInformation.getDescription());
                intent.putExtra(LOSTPOSTINFORMATION_PHONENUM, lostPostInformation.getPhoneNum());

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
                //wipe entire lostList
                lostList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting post
                    LostPostInformation lostPostInformation = postSnapshot.getValue(LostPostInformation.class);
                    //add to list
                    lostList.add(lostPostInformation);
                }

                LostList lostAdapter = new LostList(LostActivity.this, lostList);
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
            //starting Profile activity
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if (view == buttonFound){
            finish();
            //starting Found activity
            startActivity(new Intent(this, FoundActivity.class));
        }
        else if (view == buttonCreate){
            finish();
            //starting Lost activity
            startActivity(new Intent(this, LostPostActivity.class));
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
