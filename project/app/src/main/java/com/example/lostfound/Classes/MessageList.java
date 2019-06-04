package com.example.lostfound.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lostfound.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageList extends ArrayAdapter<Message>  {

    // Declare variable
    private AppCompatActivity context;

    private List<Message> messageList;

    // Pass message list
    public MessageList(AppCompatActivity context, List<Message> messageList){
        super(context, R.layout.card_message,messageList);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewMessage = inflater.inflate(R.layout.card_message, null, true);

        // Initialize
        TextView textViewUser = (TextView) listViewMessage.findViewById(R.id.textViewUser);
        TextView textViewMessage = (TextView)listViewMessage.findViewById(R.id.textViewMessage);
        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Message message = messageList.get(position);

        // Set message to textView
        textViewUser.setText(message.getUser());
        textViewMessage.setText(message.getText());

        return listViewMessage;
    }
}
