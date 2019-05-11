package com.example.lostfound.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.lostfound.Activities.RegisterActivity;
import com.example.lostfound.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LostFoundTheme);
        super.onCreate(savedInstanceState);
        finish();
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
