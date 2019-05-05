package com.example.lostfound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
/*
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
*/
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.fragment.app.FragmentActivity;


public class PostList extends ArrayAdapter<Post> {

    //private DatabaseReference databaseReference;

    private FragmentActivity context;

    private List<Post> postList;

    //private ImageView imageView;
    private TextView textViewTitle, textViewDescription;

    public PostList(FragmentActivity context, List<Post> postList){
        super(context, R.layout.layout_post_card,postList);
        this.context = context;
        this.postList = postList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_post_card, null, true);

        //imageView = (ImageView) view.findViewById(R.id.imageView);
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView)view.findViewById(R.id.textViewDescription);

        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Post post = postList.get(position);

        String userId = post.getUserId();

        /*
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/IMAGE");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                Picasso.get().load(imageUrl).resize(100,100).into(imageView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return view;
    }
}