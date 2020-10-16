package com.example.chatz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.chatz.Classes.PM_User;
import com.example.chatz.Classes.PrivateMessage;
import com.example.chatz.Classes.User;
import com.example.chatz.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("private_messages");
    public UserAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, parent, false);
        ImageView profilePictureUrl = convertView.findViewById(R.id.ContactUserPicture);
        TextView username = convertView.findViewById(R.id.ContactUsername);
        TextView description = convertView.findViewById(R.id.ContactLastMessage);
        final User user = getItem(position);
        Picasso.get().load(user.getProfilePictureUrl()).into(profilePictureUrl);
        username.setText(user.getUsername());
        description.setText(user.getDescription());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String key = reference.push().getKey();
                final PrivateMessage privateMessage = new PrivateMessage(firebaseAuth.getCurrentUser().getUid(), "Chat Invitation Text", null, false);
                firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("private_messages").orderByChild("userUID").equalTo(user.getUserUID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            reference.child(key).push().setValue(privateMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    reference = firebaseDatabase.getReference("users");
                                    PM_User pm_user = new PM_User(user.getUserUID(), key);
                                    reference.child(firebaseAuth.getCurrentUser().getUid()).child("private_messages").push().setValue(pm_user);
                                    pm_user = new PM_User(firebaseAuth.getCurrentUser().getUid(), key);
                                    reference.child(user.getUserUID()).child("private_messages").push().setValue(pm_user);
                                    ((Activity) getContext()).finish();
                                    ((Activity) getContext()).overridePendingTransition(0,0);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return convertView;
    }
}
