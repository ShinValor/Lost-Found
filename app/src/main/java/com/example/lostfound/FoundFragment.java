package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.AdapterView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoundFragment extends Fragment{

    private ListView listView;

    private DatabaseReference databaseReference;

    View view;

    private List<Post> listOfPosts;

    public static final String POST_USER = "com.example.lostfound.postuser",
                               POST_TITLE = "com.example.lostfound.posttitle",
                               POST_DESCRIPTION = "com.example.lostfound.postdescription",
                               POST_PHONE_NUMBER = "com.example.lostfound.postphonenumber",
                               POST_ID = "com.example.lostfound.postid",
                               POST_USER_ID = "com.example.lostfound.postuserid",
                               POST_ROUTE = "com.example.lostfound.postpage";

    public FoundFragment(){

    }

    public void refreshList(String search){
        FragmentActivity parentActivity = (FragmentActivity) view.getContext();
        PostList postList = new PostList(parentActivity,listOfPosts);
        postList.getFilter().filter(search);
        listView.setAdapter(postList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_found,container,false);

        listView = (ListView) view.findViewById(R.id.listView);

        listOfPosts = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfPosts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.child("INFO").getValue(Post.class);
                    listOfPosts.add(post);
                }
                Collections.reverse(listOfPosts);
                FragmentActivity parentActivity = (FragmentActivity) view.getContext();
                PostList postList = new PostList(parentActivity,listOfPosts);
                listView.setAdapter(postList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post Post = listOfPosts.get(i);
                Intent intent = new Intent(getActivity().getApplicationContext(), PostViewActivity.class);
                intent.putExtra(POST_USER, Post.getUser());
                intent.putExtra(POST_TITLE, Post.getTitle());
                intent.putExtra(POST_DESCRIPTION, Post.getDescription());
                intent.putExtra(POST_PHONE_NUMBER, Post.getPhoneNum());
                intent.putExtra(POST_ID, Post.getPostId());
                intent.putExtra(POST_USER_ID, Post.getUserId());
                intent.putExtra(POST_ROUTE, "FOUND");
                startActivity(intent);
            }
        });

        return view;
    }
}
