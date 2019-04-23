package com.example.lostfound;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.util.Log;

public class LostPostViewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView textViewUser, textViewTitle, textViewDescription;
    private Button buttonBack, buttonCall;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private String postId;
    private String imageName;
    private String imageUrl;

    private String route;

    public static final String POSTINFORMATION_PROFILE = "com.example.lostfound.lostpostinformationprofile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_post_view);
        Intent intent = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        buttonBack = (Button) findViewById(R.id.buttonBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        buttonCall = (Button) findViewById(R.id.buttonCall);

        textViewUser.setText(intent.getStringExtra(LostActivity.POSTINFORMATION_USER));
        textViewTitle.setText(intent.getStringExtra(LostActivity.POSTINFORMATION_TITLE));
        textViewDescription.setText(intent.getStringExtra(LostActivity.POSTINFORMATION_DESCRIPTION));
        buttonCall.setText(intent.getStringExtra(LostActivity.POSTINFORMATION_PHONENUM));
        postId = intent.getStringExtra(LostActivity.POSTINFORMATION_POSTID);
        route = intent.getStringExtra(LostActivity.POSTINFORMATION_PAGE);

        buttonBack.setOnClickListener(this);

        final String userPostId = intent.getStringExtra(LostActivity.POSTINFORMATION_USERID);
        textViewUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                intent.putExtra(POSTINFORMATION_PROFILE,userPostId);
                startActivity(intent);
            }
        });


        final String phoneNum = "+" + intent.getStringExtra(LostActivity.POSTINFORMATION_PHONENUM);
        buttonCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNum, null));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference("/" + route + "/" + postId + "/IMAGE");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                imageName = dataSnapshot.child("name").getValue(String.class);
                Picasso.get().load(imageUrl).fit().into(imageView);
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
