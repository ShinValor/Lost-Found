package com.example.lostfound;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class LostList extends ArrayAdapter<LostPostInformation>{

    private Activity context;

    List<LostPostInformation> lostList;

    public LostList(Activity context, List<LostPostInformation> lostList){
        super(context, R.layout.layout_lost_list,lostList);
        this.context = context;
        this.lostList = lostList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewLost = inflater.inflate(R.layout.layout_lost_list, null, true);

        TextView textViewTitle = (TextView) listViewLost.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView)listViewLost.findViewById(R.id.textViewDescription);
        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        LostPostInformation post = lostList.get(position);

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return listViewLost;
    }
}
