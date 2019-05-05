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

    private List<String> messageIds;

    private TextView textViewUser, textViewMessage;

    public MessageViewAdapter(AppCompatActivity context, List<String> messageIds){
        super(context, R.layout.layout_message_card,messageIds);
        this.context = context;
        this.messageIds = messageIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.layout_message_card, null, true);

        textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);

        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        String messageId = messageIds.get(position);

        textViewUser.setText(messageId);
        textViewMessage.setText("temp");

        return messageView;
    }
}
