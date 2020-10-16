package com.example.chatz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatz.Classes.Message;
import com.example.chatz.Classes.User;
import com.example.chatz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    DatabaseReference reference;
    public MessageAdapter(@NonNull Context context, @NonNull List<Message> objects, DatabaseReference reference) {
        super(context, 0, objects);
        this.reference = reference;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_list_item, parent, false);
        final ImageView userProfilePic = convertView.findViewById(R.id.MessageUserPicture);
        ImageView messageImage = convertView.findViewById(R.id.MessageImage);
        final TextView username = convertView.findViewById(R.id.MessageUsername);
        TextView body = convertView.findViewById(R.id.MessageBody);
        Message message = getItem(position);
        body.setText(message.getText());
        this.reference.child(message.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    username.setText(user.getUsername());
                    Picasso.get().load(user.getProfilePictureUrl()).into(userProfilePic);
                }catch (Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (message.getImageUrl() == null)
            messageImage.setVisibility(View.GONE);
        else {
            messageImage.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(message.getImageUrl()).into(messageImage);
            }catch (Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return convertView;
    }
}
