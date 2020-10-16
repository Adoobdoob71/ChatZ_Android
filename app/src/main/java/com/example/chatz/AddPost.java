package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatz.Classes.Group;
import com.example.chatz.Classes.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class AddPost extends AppCompatActivity {


    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    MaterialToolbar materialToolbar;
    EditText Title;
    EditText Body;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final int PICK_IMAGE = 100;
    Uri imageUri;
    ProgressBar progressBar;
    Group group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_add_post);
        materialToolbar = findViewById(R.id.toolbar);
        Title = findViewById(R.id.postTitle);
        Body = findViewById(R.id.postBody);
        progressBar = findViewById(R.id.ProgressBar);
        group = (Group) getIntent().getSerializableExtra("group");
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.AddPicture:
                        PickImage();
                        break;
                    case R.id.CreatePost:
                        SubmitPost();
                }
                return true;
            }
        });
        materialToolbar.setSubtitle(group.getName());
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void PickImage() {
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
            }catch (Exception error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void SubmitPost(){
        final DatabaseReference reference = firebaseDatabase.getReference("posts");
        final String key = reference.push().getKey();
        if (Title.getText().toString().trim().length() == 0 || Body.getText().toString().trim().length() == 0 || firebaseAuth.getCurrentUser() == null || imageUri == null)
            return;
        else {
            if (imageUri == null) {
                Post post = new Post(firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getEmail(), Title.getText().toString(), Body.getText().toString(), null, group.getName(), group.getImageUrl(), 0);
                reference.child(key).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            finish();
                        else
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                progressBar.setVisibility(View.VISIBLE);
                final StorageReference storageReference = firebaseStorage.getReference("images/groups").child(group.getName()).child("posts").child(key);
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
                            Post post = new Post(firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getEmail(), Title.getText().toString(), Body.getText().toString(), uri, group.getName(), group.getImageUrl(), 0);
                            reference.child(key).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            });
                        }
                        else progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
    }
}
