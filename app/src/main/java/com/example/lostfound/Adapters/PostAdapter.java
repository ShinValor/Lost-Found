package com.example.lostfound.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lostfound.Classes.Post;
import com.example.lostfound.R;

import java.util.List;

import androidx.fragment.app.FragmentActivity;


public class PostAdapter extends ArrayAdapter<Post> {

    // Declare variables

    private FragmentActivity context;

    private List<Post> postList;

    private TextView textViewTitle, textViewDescription;

    public PostAdapter(FragmentActivity context, List<Post> postList){
        super(context, R.layout.card_post,postList);
        this.context = context;
        this.postList = postList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = inflater.inflate(R.layout.card_post, null, true);

        // Initialize
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView)view.findViewById(R.id.textViewDescription);

        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Post post = postList.get(position);

        // Set the title and description to textView
        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return view;
    }
}
