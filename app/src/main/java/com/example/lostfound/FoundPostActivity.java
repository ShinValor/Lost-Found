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
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

public class FoundPostActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private Button buttonImage, buttonPost, buttonCancel;
    private EditText editTextTitle, editTextDescription;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_post);

        buttonImage = (Button) findViewById(R.id.buttonImage);
        imageView = (ImageView) findViewById(R.id.imageView);
        buttonPost = (Button) findViewById(R.id.buttonPost);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        buttonImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        buttonPost.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }

    private void onPost(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();

        // Get Information
        final String title = editTextTitle.getText().toString().trim();
        final String desc = editTextDescription.getText().toString().trim();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(userId)) {
                        UserInformation userInformation = postSnapshot.getValue(UserInformation.class);
                        FoundPostInformation foundPostInformation = new FoundPostInformation(userInformation.getName(),title,desc,userInformation.getPhoneNum(),userId);
                        databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND");
                        databaseReference.child(title).setValue(foundPostInformation);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonPost){
            //post to database
            onPost();
            Toast.makeText(this, "Posted", Toast.LENGTH_LONG).show();
            finish();
            //starting Found activity
            startActivity(new Intent(this, FoundActivity.class));
        }
        else if (view == buttonCancel){
            finish();
            //starting Found activity
            startActivity(new Intent(this, FoundActivity.class));
        }
    }
}
