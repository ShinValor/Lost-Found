package com.example.lostfound;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class FoundList extends ArrayAdapter<FoundPostInformation>{

    private Activity context;

    List<FoundPostInformation> foundList;

    public FoundList(Activity context, List<FoundPostInformation> foundList){
        super(context, R.layout.layout_found_list,foundList);
        this.context = context;
        this.foundList = foundList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewFound = inflater.inflate(R.layout.layout_found_list, null, true);

        TextView textViewTitle = (TextView) listViewFound.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView)listViewFound.findViewById(R.id.textViewDescription);
        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        FoundPostInformation post = foundList.get(position);

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return listViewFound;
    }

}
