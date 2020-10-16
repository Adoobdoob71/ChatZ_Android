package com.example.chatz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.chatz.Classes.PrivateMessage;
import com.example.chatz.Classes.User;
import com.example.chatz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PrivateMessageAdapter extends ArrayAdapter<PrivateMessage> {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("users");
    public PrivateMessageAdapter(@NonNull Context context, @NonNull List<PrivateMessage> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PrivateMessage privateMessage = getItem(position);
        if (privateMessage.getUserUID().equals(firebaseAuth.getCurrentUser().getUid()))
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.private_message_user_item, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.private_message_guest_item, parent, false);

        if (privateMessage.getUserUID().equals(firebaseAuth.getCurrentUser().getUid())) {
            TextView messageText = convertView.findViewById(R.id.TextMessage);
            ImageView UserImage = convertView.findViewById(R.id.PrivateMessageUserImage);
            if (privateMessage.getText().trim().length() == 0)
                messageText.setVisibility(View.GONE);
            else
                messageText.setText(privateMessage.getText());
            if (privateMessage.getImageUrl() != null){
                Picasso.get().load(privateMessage.getImageUrl()).into(UserImage);
                UserImage.setVisibility(View.VISIBLE);
            }
        }
        else {
            final ImageView profilePicture = convertView.findViewById(R.id.GuestProfilePicture);
            TextView messageText = convertView.findViewById(R.id.TextMessage);
            ImageView UserImage = convertView.findViewById(R.id.PrivateMessageGuestImage);
            if (privateMessage.getText().trim().length() == 0)
                messageText.setVisibility(View.GONE);
            else
                messageText.setText(privateMessage.getText());
            if (privateMessage.getImageUrl() != null){
                Picasso.get().load(privateMessage.getImageUrl()).into(UserImage);
                UserImage.setVisibility(View.VISIBLE);
            }
            reference.child(privateMessage.getUserUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfilePictureUrl()).into(profilePicture);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (privateMessage.getEdited()) {
            TextView edited = convertView.findViewById(R.id.Edited);
            edited.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
