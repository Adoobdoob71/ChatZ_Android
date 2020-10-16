package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatz.Adapters.PostAdapter;
import com.example.chatz.Classes.Post;
import com.example.chatz.Classes.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileScreen extends AppCompatActivity {


    ImageView profilePicture;
    TextView username;
    TextView email;
    TextView description;
    MaterialToolbar toolbar;

    ListView listView;
    ArrayList<Post> arrayList = new ArrayList<>();
    PostAdapter postAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_profile_screen);
        postAdapter = new PostAdapter(this, arrayList);
        listView = findViewById(R.id.ProfileScreenListView);
        profilePicture = findViewById(R.id.ProfilePicture);
        username = findViewById(R.id.Username);
        email = findViewById(R.id.Email);
        description = findViewById(R.id.Description);
        toolbar = findViewById(R.id.Toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setAdapter(postAdapter);
        firebaseDatabase.getReference("users").child(getIntent().getStringExtra("userUID")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePictureUrl()).into(profilePicture);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                description.setText(user.getDescription());
                LoadPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void LoadPosts(){
        DatabaseReference postReference = firebaseDatabase.getReference("posts");
        postReference.orderByChild("userUID").equalTo(user.getUserUID()).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setKey(dataSnapshot.getKey());
                    arrayList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
