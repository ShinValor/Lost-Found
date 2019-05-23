package com.example.lostfound.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostfound.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    // Declare variables

    private FirebaseAuth firebaseAuth;

    private TextView textViewSignup, textViewResetPassword;
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignin;

    private ProgressDialog progressDialog;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textViewSignup = (TextView) findViewById(R.id.textViewSignup);
        textViewResetPassword = (TextView) findViewById(R.id.textViewResetPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignin = (Button) findViewById(R.id.buttonSignin);

        progressDialog = new ProgressDialog(this);

        // Set Listeners
        textViewSignup.setOnClickListener(this);
        textViewResetPassword.setOnClickListener(this);
        buttonSignin.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        // If email is empty, return
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        // If email is empty, return
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Logging Please Wait...");
        progressDialog.show();

        // Sign in with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        // If email is not verified, verify
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (!user.isEmailVerified()){
                            Toast.makeText(LoginActivity.this, "Please Verify email.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // start main activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                    else {
                        // Failed to log in
                        Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public void onClick(View view){
        if (view == buttonSignin){
            userLogin();
        }
        else if (view == textViewSignup){
            finish();
            startActivity(new Intent(this, RegisterActivity.class ));
        }
        else if (view == textViewResetPassword){
            // Reset password through email
            firebaseAuth.getInstance().sendPasswordResetEmail("frodo1642@gmail.com.com")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Email Sent", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }
}
