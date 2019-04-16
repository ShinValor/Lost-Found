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

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private List<PostInformation> foundList;

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

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
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

        listViewFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PostInformation postInformation = foundList.get(i);

                Intent intent = new Intent(getApplicationContext(), FoundPostViewActivity.class);

                intent.putExtra(FOUNDPOSTINFORMATION_USER, postInformation.getUser());
                intent.putExtra(FOUNDPOSTINFORMATION_TITLE, postInformation.getTitle());
                intent.putExtra(FOUNDPOSTINFORMATION_DESCRIPTION, postInformation.getDescription());
                intent.putExtra(FOUNDPOSTINFORMATION_PHONENUM, postInformation.getPhoneNum());
                intent.putExtra(FOUNDPOSTINFORMATION_POSTID, postInformation.getPostId());
                intent.putExtra(FOUNDPOSTINFORMATION_USERID, postInformation.getUserId());

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                foundList.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    PostInformation postInformation = postSnapshot.child("INFO").getValue(PostInformation.class);

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
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if (view == buttonLost){
            finish();
            startActivity(new Intent(this, LostActivity.class));
        }
        else if (view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if (view == buttonCreate){
            finish();
            startActivity(new Intent(this, FoundPostActivity.class));
        }
    }
}
