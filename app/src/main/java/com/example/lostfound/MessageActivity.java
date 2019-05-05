package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ListView listViewMessage;
    private EditText edittext_chatbox;
    private Button buttonBack, button_chatbox_send;

    private String userId, messageUserId, messageId;

    private List<Message> messageList;

    void addNotification(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender("lostfoundee32f@gmail.com","A24518190d");
                    //sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");
                    sender.sendMail("Found Item", "You have an item that someone lost.","lostfoundee32f@gmail.com","frodo1642@gmail.com");
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_message);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        Intent intent = getIntent();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        messageUserId = intent.getStringExtra(ProfileViewActivity.POST_USER_ID);

        messageList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(messageUserId)) {
                        messageId = dataSnapshot.child(messageUserId).getValue(String.class);
                    }
                }
                if (messageId == null){
                    addNotification();
                    String key = databaseReference.push().getKey();
                    databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
                    databaseReference.child(messageUserId).setValue(key);
                    databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUserId + "/CHAT/");
                    databaseReference.child(userId).setValue(key);
                    databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/");
                    databaseReference.child(key).setValue(false);
                }
                databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messageList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Message message = postSnapshot.getValue(Message.class);
                            messageList.add(message);
                        }
                        MessageAdapter messageAdapter = new MessageAdapter(MessageActivity.this, messageList);
                        listViewMessage.setAdapter(messageAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        edittext_chatbox = (EditText) findViewById(R.id.edittext_chatbox);
        button_chatbox_send = (Button) findViewById(R.id.button_chatbox_send);
        listViewMessage = (ListView) findViewById(R.id.listViewMessage);

        button_chatbox_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == button_chatbox_send){
            databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/INFO/");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String content = edittext_chatbox.getText().toString();
                    if (!content.isEmpty()){
                        edittext_chatbox.setText("");
                        User user = dataSnapshot.getValue(User.class);
                        Message message = new Message(content,user.getName());
                        databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
                        String postUID = databaseReference.push().getKey();
                        databaseReference.child(postUID).setValue(message);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
