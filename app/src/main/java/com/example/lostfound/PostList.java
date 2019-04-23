package com.example.lostfound;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class PostList extends ArrayAdapter<Post> {

    private Activity context;

    private List<Post> postList;

    public PostList(Activity context, List<Post> postList){
        super(context, R.layout.layout_post_list,postList);
        this.context = context;
        this.postList = postList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewPost = inflater.inflate(R.layout.layout_post_list, null, true);

        TextView textViewTitle = (TextView) listViewPost.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView)listViewPost.findViewById(R.id.textViewDescription);
        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Post post = postList.get(position);

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return listViewPost;
    }
}