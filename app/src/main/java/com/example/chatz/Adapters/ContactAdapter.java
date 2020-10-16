package com.example.chatz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatz.Classes.PM_User;
import com.example.chatz.Classes.PrivateMessage;
import com.example.chatz.Classes.User;
import com.example.chatz.PrivateChatWindow;
import com.example.chatz.R;
import com.example.chatz.SearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<PM_User> {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference("private_messages");
    private DatabaseReference reference2 = firebaseDatabase.getReference("users");
    public ContactAdapter(@NonNull Context context, @NonNull List<PM_User> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, parent, false);
        final View view = convertView;
        final ImageView profilePicture = view.findViewById(R.id.ContactUserPicture);
        final TextView contactUsername = view.findViewById(R.id.ContactUsername);
        final TextView lastMessage = view.findViewById(R.id.ContactLastMessage);
        final PM_User pm_user = getItem(position);
        reference2.child(pm_user.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePictureUrl()).into(profilePicture);
                contactUsername.setText(user.getUsername());
                reference.child(pm_user.getPrivate_messages_ID()).limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                PrivateMessage privateMessage = dataSnapshot.getValue(PrivateMessage.class);
                                lastMessage.setText(privateMessage.getText());
                            }
                        }catch (Exception error) {
                            lastMessage.setText(user.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PrivateChatWindow.class);
                intent.putExtra("userUID", pm_user.getUserUID());
                intent.putExtra("chatID", pm_user.getPrivate_messages_ID());
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}
