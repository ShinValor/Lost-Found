package com.example.lostfound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageViewAdapter extends ArrayAdapter<String> {

    private AppCompatActivity context;

    private List<String> messageIds, messageUsers;

    private TextView textViewUser, textViewMessage;

    public MessageViewAdapter(AppCompatActivity context, List<String> messageUsers){
        //super(context, R.layout.layout_message_card,messageIds);
        super(context, R.layout.layout_message_card,messageUsers);
        this.context = context;
        //this.messageIds = messageIds;
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
        //String messageId = messageIds.get(position);

        textViewUser.setText(messageUser);
        //textViewMessage.setText("temp");

        return messageView;
    }
}
