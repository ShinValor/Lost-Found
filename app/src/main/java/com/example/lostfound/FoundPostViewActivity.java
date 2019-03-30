package com.example.lostfound;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class FoundPostViewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView textViewUser, textViewTitle, textViewDescription;
    private Button buttonBack, buttonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_post_view);
        Intent intent = getIntent();

        buttonBack = (Button) findViewById(R.id.buttonBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        buttonCall = (Button) findViewById(R.id.buttonCall);

        textViewUser.setText(intent.getStringExtra(FoundActivity.FOUNDPOSTINFORMATION_USER));
        textViewTitle.setText(intent.getStringExtra(FoundActivity.FOUNDPOSTINFORMATION_TITLE));
        textViewDescription.setText(intent.getStringExtra(FoundActivity.FOUNDPOSTINFORMATION_DESCRIPTION));
        buttonCall.setText(intent.getStringExtra(FoundActivity.FOUNDPOSTINFORMATION_PHONENUM));

        buttonBack.setOnClickListener(this);

        textViewUser.setOnClickListener(this);

        final String yourNumber = "+" + intent.getStringExtra(FoundActivity.FOUNDPOSTINFORMATION_PHONENUM);
        buttonCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", yourNumber, null));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonBack) {
            // stop current activity
            finish();
            //starting Lost activity
            startActivity(new Intent(this, FoundActivity.class));
        } else if (view == textViewUser) {
            // stop current activity
            finish();
            //starting Lost activity
            startActivity(new Intent(this, ViewProfileActivity.class));
        }
    }
}
