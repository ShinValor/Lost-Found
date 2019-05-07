package com.example.lostfound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageViewAdapter extends ArrayAdapter<String> {

    private DatabaseReference databaseReference;

    private AppCompatActivity context;

    private List<String> messageIds, messageUsers;

    private TextView textViewUser, textViewMessage;

    public MessageViewAdapter(AppCompatActivity context, List<String> messageUsers){
        super(context, R.layout.layout_message_card,messageUsers);
        this.context = context;
        this.messageUsers = messageUsers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.layout_message_card, null, true);

        textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);

        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        String messageUser = messageUsers.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("INFO").getValue(User.class);
                textViewUser.setText(user.getName());
                textViewMessage.setText("New Messages");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return messageView;
    }
}
