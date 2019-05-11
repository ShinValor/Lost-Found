package com.example.lostfound.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lostfound.Classes.Message;
import com.example.lostfound.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageAdapter extends ArrayAdapter<Message>  {

    private AppCompatActivity context;

    private List<Message> messageList;

    private TextView textViewUser, textViewMessage;

    public MessageAdapter(AppCompatActivity context, List<Message> messageList){
        super(context, R.layout.layout_message_card,messageList);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.layout_message_card, null, true);

        textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);

        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Message message = messageList.get(position);

        textViewUser.setText(message.getUser());
        textViewMessage.setText(message.getText());

        return messageView;
    }
}
