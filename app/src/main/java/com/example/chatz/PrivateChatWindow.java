package com.example.chatz;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatz.Adapters.PrivateMessageAdapter;
import com.example.chatz.Classes.PrivateMessage;
import com.example.chatz.Classes.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class PrivateChatWindow extends AppCompatActivity {

    ImageView profilePicture;
    TextView title;
    TextView description;
    ListView listView;
    EditText messageEditText;
    String userUID;
    String chatID;
    ImageButton submitButton;
    ImageButton attachmentButton;
    ImageButton removeButton;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("private_messages");
    ArrayList<PrivateMessage> arrayList = new ArrayList<>();
    PrivateMessageAdapter privateMessageAdapter;

    final int PICK_IMAGE = 100;
    Uri imageUri;
    LinearLayout messageImage;
    ProgressBar progressBar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_private_chat_window);
        profilePicture = findViewById(R.id.ProfilePicture);
        title = findViewById(R.id.Title);
        description = findViewById(R.id.UserDescription);
        listView = findViewById(R.id.PrivateChatWindowListView);
        messageEditText = findViewById(R.id.MessageEditText);
        progressBar = findViewById(R.id.ProgressBar);
        submitButton = findViewById(R.id.SendMessageButton);
        removeButton = findViewById(R.id.RemoveImage);
        messageImage = findViewById(R.id.MessageImage);
        attachmentButton = findViewById(R.id.AddPictureButton);
        userUID = getIntent().getStringExtra("userUID");
        chatID = getIntent().getStringExtra("chatID");
        DatabaseReference reference2 = firebaseDatabase.getReference("users");
        reference2.child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    User user = snapshot.getValue(User.class);
                    title.setText(user.getUsername());
                    description.setText(user.getDescription());
                    Picasso.get().load(user.getProfilePictureUrl()).into(profilePicture);
                }catch (Exception error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        privateMessageAdapter = new PrivateMessageAdapter(this, arrayList);
        listView.setAdapter(privateMessageAdapter);
        LoadMessages();
    }

    public void LoadMessages() {
        reference.child(chatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                PrivateMessage privateMessage = snapshot.getValue(PrivateMessage.class);
                arrayList.add(privateMessage);
                privateMessageAdapter.notifyDataSetChanged();
                listView.smoothScrollToPosition(listView.getBottom());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void AddMessage(View view) {
        submitButton.setEnabled(false);
        attachmentButton.setEnabled(false);
        removeButton.setEnabled(false);
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final String key = reference.child(chatID).push().getKey();
            final StorageReference storageReference = firebaseStorage.getReference("images/private_messages").child(key);
            UploadTask uploadTask = storageReference.putFile(imageUri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    long progress = taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() * 100;
                    progressBar.setProgress((int) progress);
                }
            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful())
                        return storageReference.getDownloadUrl();
                    throw task.getException();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String uri = task.getResult().toString();
                        PrivateMessage privateMessage = new PrivateMessage(firebaseAuth.getCurrentUser().getUid(), messageEditText.getText().toString().trim(), uri, false);
                        reference.child(chatID).child(key).setValue(privateMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                listView.smoothScrollToPosition(listView.getBottom());
                                messageEditText.setText("");
                                progressBar.setVisibility(View.GONE);
                                messageImage.setVisibility(View.GONE);
                                imageUri = null;
                                submitButton.setEnabled(true);
                                attachmentButton.setEnabled(true);
                                removeButton.setEnabled(true);
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);
                        attachmentButton.setEnabled(true);
                        removeButton.setEnabled(true);
                    }
                }
            });
        }
        else if (messageEditText.getText().toString().trim().length() != 0) {
            PrivateMessage privateMessage = new PrivateMessage(firebaseAuth.getCurrentUser().getUid(), messageEditText.getText().toString().trim(), null, false);
            reference.child(chatID).push().setValue(privateMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    submitButton.setEnabled(true);
                    attachmentButton.setEnabled(true);
                    removeButton.setEnabled(true);
                    messageEditText.setText("");
                    progressBar.setVisibility(View.GONE);
                    messageImage.setVisibility(View.GONE);
                    imageUri = null;
                }
            });
        }
    }

    public void PickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Drawable drawable = Drawable.createFromStream(inputStream, imageUri.toString());
                messageImage.setBackground(drawable);
                messageImage.setVisibility(View.VISIBLE);
            }catch (Exception error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void RemoveImage(View view) {
        messageImage.setVisibility(View.GONE);
        imageUri = null;
    }

    public void Finish(View view) {
        finish();
    }
}
