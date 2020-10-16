package com.example.chatz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chatz.AddPost;
import com.example.chatz.ChatWindow;
import com.example.chatz.Classes.Group;
import com.example.chatz.Classes.ImageHandling;
import com.example.chatz.Classes.Post;
import com.example.chatz.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {

    ConstraintLayout constraintLayout;
    BottomSheetBehavior bottomSheetBehavior;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("posts");
    public GroupAdapter(@NonNull Context context, @NonNull List<Group> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_list_item, parent, false);
        ImageView groupImage = convertView.findViewById(R.id.GroupImage);
        TextView groupName = convertView.findViewById(R.id.GroupName);
        TextView groupDescription = convertView.findViewById(R.id.GroupDescription);
        final Group group = getItem(position);
        Picasso.get().load(group.getImageUrl()).into(groupImage);
//        new ImageHandling(groupImage).execute(group.getImageUrl());
        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadBottomSheet(group);
            }
        });
        return convertView;
    }

    public void LoadBottomSheet(final Group group) {
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
        Button postButton = constraintLayout.findViewById(R.id.PostButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPost.class);
                intent.putExtra("group", group);
                activity.startActivity(intent);
            }
        });
        final ListView listView = constraintLayout.findViewById(R.id.BottomSheetsListView);
        final ArrayList<Post> arrayList = new ArrayList<>();
        final PostAdapter postAdapter = new PostAdapter(getContext(), arrayList);
        listView.setAdapter(postAdapter);
        reference.orderByChild("groupName").equalTo(group.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
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
