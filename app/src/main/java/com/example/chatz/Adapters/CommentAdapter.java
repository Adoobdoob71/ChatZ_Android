package com.example.chatz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatz.Classes.Comment;
import com.example.chatz.Classes.User;
import com.example.chatz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    DatabaseReference reference;
    public CommentAdapter(@NonNull Context context, @NonNull List<Comment> objects, DatabaseReference reference) {
        super(context, 0, objects);
        this.reference = reference;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_list_item, parent, false);

        final TextView username = convertView.findViewById(R.id.CommentUsername);
        TextView body = convertView.findViewById(R.id.CommentBody);
        TextView email = convertView.findViewById(R.id.CommentEmail);
        final ImageView profilePicture = convertView.findViewById(R.id.CommentProfileImage);
        LinearLayout commentUserButton = convertView.findViewById(R.id.CommentUserButton);
        final Comment comment = getItem(position);
        body.setText(comment.getText());
        email.setText(comment.getEmail());
        this.reference.child(comment.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    username.setText(user.getUsername());
                    Picasso.get().load(user.getProfilePictureUrl()).into(profilePicture);
                } catch (Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), comment.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

}
