package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;

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