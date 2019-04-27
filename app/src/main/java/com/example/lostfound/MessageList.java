package com.example.lostfound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MessageList extends ArrayAdapter<Message>  {

    private AppCompatActivity context;

    private List<Message> messageList;

    public MessageList(AppCompatActivity context, List<Message> messageList){
        super(context, R.layout.layout_message_list,messageList);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewMessage = inflater.inflate(R.layout.layout_message_list, null, true);

        TextView textViewUser = (TextView) listViewMessage.findViewById(R.id.textViewUser);
        TextView textViewMessage = (TextView)listViewMessage.findViewById(R.id.textViewMessage);
        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Message convo = messageList.get(position);

        textViewUser.setText(convo.getUser());
        textViewMessage.setText(convo.getText());

        return listViewMessage;
    }
}
