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

    private ListView listViewLost;
    private Button buttonProfile, buttonFound, buttonCreate, buttonLogout;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private List<PostInformation> lostList;

    public static final String LOSTPOSTINFORMATION_USER = "com.example.lostfound.lostpostinformationuser",
                               LOSTPOSTINFORMATION_TITLE = "com.example.lostfound.lostpostinformationtitle",
                               LOSTPOSTINFORMATION_DESCRIPTION = "com.example.lostfound.lostpostinformationdescription",
                               LOSTPOSTINFORMATION_PHONENUM = "com.example.lostfound.lostpostinformationphonenum",
                               LOSTPOSTINFORMATION_POSTID = "com.example.lostfound.lostpostinformationpostid",
                               LOSTPOSTINFORMATION_USERID = "com.example.lostfound.lostpostinformationuserid";

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
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        listViewLost = (ListView) findViewById(R.id.listViewLost);

        lostList = new ArrayList<>();

        buttonProfile.setOnClickListener(this);
        buttonFound.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        listViewLost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PostInformation postInformation = lostList.get(i);

                Intent intent = new Intent(getApplicationContext(), LostPostViewActivity.class);

                intent.putExtra(LOSTPOSTINFORMATION_USER, postInformation.getUser());
                intent.putExtra(LOSTPOSTINFORMATION_TITLE, postInformation.getTitle());
                intent.putExtra(LOSTPOSTINFORMATION_DESCRIPTION, postInformation.getDescription());
                intent.putExtra(LOSTPOSTINFORMATION_PHONENUM, postInformation.getPhoneNum());
                intent.putExtra(LOSTPOSTINFORMATION_POSTID, postInformation.getPostId());
                intent.putExtra(LOSTPOSTINFORMATION_USERID, postInformation.getUserId());

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

                lostList.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    PostInformation postInformation = postSnapshot.child("INFO").getValue(PostInformation.class);

                    lostList.add(postInformation);
                }

                PostList lostAdapter = new PostList(LostActivity.this, lostList);
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
            finish();
            startActivity(new Intent(this, FoundActivity.class));
        }
        else if (view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if (view == buttonCreate){
            finish();
            startActivity(new Intent(this, LostPostActivity.class));
        }
    }
}
