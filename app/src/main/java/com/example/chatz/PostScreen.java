package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatz.Adapters.CommentAdapter;
import com.example.chatz.Classes.Comment;
import com.example.chatz.Classes.Post;
import com.example.chatz.Classes.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostScreen extends AppCompatActivity {


    MaterialToolbar materialToolbar;
    ListView listView;
    ArrayList<Comment> arrayList;
    CommentAdapter commentAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView groupImage;
    ImageView postImage;
    TextView groupName;
    TextView postTitle;
    TextView body;
    View postCard;
    FloatingActionButton floatingActionButton;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_post_screen);

        //Gathering data
        final Post post = (Post) getIntent().getSerializableExtra("post");

        //Initializing variables
        postCard = getLayoutInflater().inflate(R.layout.comment_list_header, null);
        materialToolbar = findViewById(R.id.PostScreenToolbar);
        listView = findViewById(R.id.PostScreenListView);
        swipeRefreshLayout = findViewById(R.id.PostScreenRefresh);
        groupImage = postCard.findViewById(R.id.PostScreenGroupImage);
        postImage = postCard.findViewById(R.id.PostScreenPostImage);
        groupName = postCard.findViewById(R.id.PostScreenGroupName);
        postTitle = postCard.findViewById(R.id.PostScreenTitle);
        floatingActionButton = findViewById(R.id.FloatingActionButton);
        body = postCard.findViewById(R.id.postBody);

        //Setting data into variables
//        if (post.getImageUrl() == null)
//            postImage.setVisibility(View.GONE);
        groupName.setText(post.getGroupName());
        postTitle.setText(post.getTitle());
        body.setText(post.getBody());
        Picasso.get().load(post.getImageUrl()).into(postImage);
        Picasso.get().load(post.getGroupImage()).into(groupImage);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), AddComment.class);
                intent.putExtra("post", post);
                startActivity(intent);
                return true;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.smoothScrollToPosition(0);
            }
        });

        //Setting adapter
        arrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, arrayList, firebaseDatabase.getReference("users"));
        listView.setAdapter(commentAdapter);
        listView.addHeaderView(postCard);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                LoadComments(post);
            }
        });

        //Loading Comments
        LoadComments(post);
    }

    public void LoadComments(Post post){
        DatabaseReference reference = firebaseDatabase.getReference("posts").child(post.getKey()).child("comments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    arrayList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
