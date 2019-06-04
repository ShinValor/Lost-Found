package com.example.lostfound.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lostfound.Classes.Message;
import com.example.lostfound.Classes.User;
import com.example.lostfound.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageViewAdapter extends ArrayAdapter<String> {

    // Declare variable

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private AppCompatActivity context;

    private List<String> messageIds, messageUsers;

    private TextView textViewUser, textViewMessage;

    private String messageId;

    // Pass in message list
    public MessageViewAdapter(AppCompatActivity context, List<String> messageUsers){
        super(context, R.layout.card_message,messageUsers);
        this.context = context;
        this.messageUsers = messageUsers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.card_message, null, true);

        // Initialize
        textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);

        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        String messageUser = messageUsers.get(position);

        firebaseAuth = FirebaseAuth.getInstance();

        // Get the user name who sent the message
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("INFO").getValue(User.class);
                textViewUser.setText(user.getName());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get the message
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser + "/CHAT/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String findUser = postSnapshot.getKey();
                    if (userId.equals(findUser)){
                        messageId = postSnapshot.getValue(String.class);
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Message message = postSnapshot.getValue(Message.class);
                                textViewMessage.setText(message.getText());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return messageView;
    }
}
