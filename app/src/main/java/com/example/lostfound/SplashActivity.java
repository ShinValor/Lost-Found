package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LostFoundTheme);
        super.onCreate(savedInstanceState);
        finish();
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
