package com.example.lostfound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonBack){
            // stop current activity
            finish();
            //starting Lost activity
            startActivity(new Intent(this, LostActivity.class));
        }
    }
}
