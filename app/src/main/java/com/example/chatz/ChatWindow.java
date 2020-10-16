package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatz.Adapters.MessageAdapter;
import com.example.chatz.Classes.Group;
import com.example.chatz.Classes.Message;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    ListView listView;
    MessageAdapter messageAdapter;
    ArrayList<Message> arrayList;
    EditText editText;
    LinearLayout toolbar;
    TextView title;
    LinearLayout backButton;
    ImageView groupImage;
    ImageButton pickImage;
    ImageButton sendButton;
    ImageButton removeButton;

    Group group;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference messageReference;

    final int PICK_IMAGE = 100;
    Uri imageUri;
    LinearLayout messageImage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_chat_window);
        group = (Group) getIntent().getSerializableExtra("group");
        messageReference = firebaseDatabase.getReference("groups").child(group.getName()).child("chat");
        toolbar = findViewById(R.id.toolbar);
        messageImage = findViewById(R.id.MessageImage);
        groupImage = findViewById(R.id.GroupImage);
        backButton = findViewById(R.id.BackButton);
        title = findViewById(R.id.Title);
        sendButton = findViewById(R.id.SendMessageButton);
        removeButton = findViewById(R.id.RemoveImage);
        pickImage = findViewById(R.id.AddPictureButton);
        progressBar = findViewById(R.id.ProgressBar);
        editText = findViewById(R.id.MessageEditText);
        Picasso.get().load(group.getImageUrl()).into(groupImage);
        title.setText(group.getName());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = findViewById(R.id.ChatWindowListView);
        arrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, arrayList, firebaseDatabase.getReference("users"));
        listView.setAdapter(messageAdapter);
        LoadMessages(group);
     }

    public void LoadMessages(final Group group) {
        DatabaseReference reference = firebaseDatabase.getReference("groups").child(group.getName()).child("chat");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                arrayList.add(message);
                messageAdapter.notifyDataSetChanged();
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
        sendButton.setEnabled(false);
        removeButton.setEnabled(false);
        pickImage.setEnabled(false);
        final String key = messageReference.push().getKey();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String body = editText.getText().toString();
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = firebaseStorage.getReference("images/groups").child(group.getName()).child("chat").child(key);
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
                        Message message = new Message(user.getEmail(), user.getUid(), body.trim(), uri);
                        messageReference.child(key).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                editText.setText("");
                                progressBar.setVisibility(View.GONE);
                                messageImage.setVisibility(View.GONE);
                                imageUri = null;
                                listView.smoothScrollToPosition(listView.getBottom());
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(body.trim().length() != 0){
                Message message = new Message(user.getEmail(), user.getUid(), body.trim(), null);
                editText.setText("");
                messageReference.child(key).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        messageAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(listView.getBottom());
                }
            });
        }
        removeButton.setEnabled(true);
        sendButton.setEnabled(true);
        pickImage.setEnabled(true);
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
}
