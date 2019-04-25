package com.example.lostfound;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;
import java.util.List;
import android.util.Log;

public class PostList extends ArrayAdapter<Post> {

    private DatabaseReference databaseReference;

    private Activity context;

    private List<Post> postList;

    private ImageView imageView;
    private TextView textViewTitle, textViewDescription;

    public PostList(Activity context, List<Post> postList){
        super(context, R.layout.layout_post_list,postList);
        this.context = context;
        this.postList = postList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewPost = inflater.inflate(R.layout.layout_post_list, null, true);

        imageView = (ImageView) listViewPost.findViewById(R.id.imageView);
        textViewTitle = (TextView) listViewPost.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView)listViewPost.findViewById(R.id.textViewDescription);

        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Post post = postList.get(position);

        String userId = post.getUserId();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/IMAGE");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                Picasso.get().load(imageUrl).fit().into(imageView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return listViewPost;
    }
}