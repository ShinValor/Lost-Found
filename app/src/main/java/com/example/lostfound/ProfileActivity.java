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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Firebase auth object
    private FirebaseAuth firebaseAuth;

    // Firebase database reference
    private DatabaseReference databaseReference;

    //view objects
    private ImageView imageView;
    private TextView textViewUserEmail;
    private EditText editTextName, editTextSchool, editTextId, editTextPhoneNum;
    private Button buttonSave, buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in that means current user will return null
        if (firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Initializing views
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSchool = (EditText) findViewById(R.id.editTextSchool);
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextPhoneNum = (EditText) findViewById(R.id.editTextPhoneNum);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS");

        // Getting current user and display logged in user name
        FirebaseUser user = firebaseAuth.getCurrentUser();
        textViewUserEmail.setText(user.getEmail());

        // Adding listener to button
        buttonBack.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
    }

    private void saveUserInformation(){

        // Get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String name = editTextName.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String phoneNum = editTextPhoneNum.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name,school,id,user.getEmail(),phoneNum);

        // Create a node using unique id and set the value of that node to userInformation
        databaseReference.child(user.getUid().toString()).setValue(userInformation);
        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonBack){
            //closing activity
            finish();
            //starting lost activity
            startActivity(new Intent(this, LostActivity.class));
        }
        else if (view == buttonSave){
            saveUserInformation();
        }
    }

}
