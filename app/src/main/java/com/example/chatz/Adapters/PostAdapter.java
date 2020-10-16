package com.example.chatz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.chatz.*;
import com.example.chatz.Classes.Group;
import com.example.chatz.Classes.Post;
import com.example.chatz.R;
import com.example.chatz.Classes.User;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<Post> {

    ConstraintLayout constraintLayout;
    BottomSheetBehavior bottomSheetBehavior;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = database.getReference("users");
    public PostAdapter(@NonNull Context context, @NonNull ArrayList<Post> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_list_item, parent, false);

        ImageView groupImage = convertView.findViewById(R.id.GroupImage);
        ImageView postImage = convertView.findViewById(R.id.PostImage);
        final ImageView userImage = convertView.findViewById(R.id.UserProfilePicture);
        TextView title = convertView.findViewById(R.id.Title);
        final TextView groupName = convertView.findViewById(R.id.GroupName);
        final TextView username = convertView.findViewById(R.id.Username);
        final LinearLayout userButton = convertView.findViewById(R.id.UserButton);
        Button readMore = convertView.findViewById(R.id.ReadMore);
        final Post post = getItem(position);
        Picasso.get().load(post.getGroupImage()).into(groupImage);
        Picasso.get().load(post.getImageUrl()).into(postImage);
//        new ImageHandling(groupImage).execute(post.getGroupImage());
//        new ImageHandling(postImage).execute(post.getImageUrl());
        title.setText(post.getTitle());
        groupName.setText(post.getGroupName());
        final DatabaseReference groupReference = database.getReference("groups").child(post.getGroupName());
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group group = snapshot.getValue(Group.class);
                        group.setName(post.getGroupName());
                        try{
                            LoadBottomSheets(group);
                        }catch(Exception error) {
                            Log.e(error.getLocalizedMessage(), error.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        this.reference.child(post.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfilePictureUrl()).into(userImage);
//                    new ImageHandling(userImage).execute(user.getProfilePictureUrl());
                    username.setText(user.getUsername());
                }catch (Exception error) {
                    username.setText("Error: Deleted");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostScreen.class);
                intent.putExtra("post", post);
                getContext().startActivity(intent);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileScreen.class);
                intent.putExtra("userUID", post.getUserUID());
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    public void LoadBottomSheets(final Group group) {
        DatabaseReference groupReference = database.getReference("posts");
        final Activity activity = (Activity) getContext();
        constraintLayout = activity.findViewById(R.id.BottomSheetLayout);
        TextView groupName = constraintLayout.findViewById(R.id.GroupName);
        TextView groupDescription = constraintLayout.findViewById(R.id.GroupDescription);
        ImageView groupImage = constraintLayout.findViewById(R.id.GroupImage);
        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());
        Picasso.get().load(group.getImageUrl()).into(groupImage);
//        new ImageHandling(groupImage).execute(group.getImageUrl());
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        ImageButton button = constraintLayout.findViewById(R.id.ChatButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatWindow.class);
                intent.putExtra("group", group);
                activity.startActivity(intent);
            }
        });
        final Button postButton = constraintLayout.findViewById(R.id.PostButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPost.class);
                intent.putExtra("group", group);
                activity.startActivity(intent);
            }
        });
        if (this.firebaseAuth.getCurrentUser() == null)
            postButton.setEnabled(false);
        ListView listView = constraintLayout.findViewById(R.id.BottomSheetsListView);
        final ArrayList<Post> arrayList = new ArrayList<>();
        final PostAdapter postAdapter = new PostAdapter(getContext(), arrayList);
        listView.setAdapter(postAdapter);
        groupReference.orderByChild("groupName").equalTo(group.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        post.setKey(dataSnapshot.getKey());
                        arrayList.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                }catch (Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
