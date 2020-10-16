package com.example.chatz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatz.Classes.Comment;
import com.example.chatz.Classes.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddComment extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    MaterialToolbar materialToolbar;
    EditText commentBody;
    MenuItem submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_add_comment);

        //Gathering data
        final Post post = (Post) getIntent().getSerializableExtra("post");

        //Initializing variables
        materialToolbar = findViewById(R.id.AddCommentToolbar);
        commentBody = findViewById(R.id.AddCommentEditText);

        //Setting events
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        materialToolbar.setSubtitle(post.getGroupName());
        submit = materialToolbar.getMenu().findItem(R.id.Submit);
        if (firebaseAuth.getCurrentUser() == null)
            submit.setEnabled(false);
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SubmitComment(post);
                return true;
            }
        });
    }

    public void SubmitComment(final Post post) {
        if (commentBody.getText().toString().trim().length() != 0) {
            submit.setEnabled(false);
            DatabaseReference reference = firebaseDatabase.getReference("posts").child(post.getKey()).child("comments");
            Comment comment = new Comment(commentBody.getText().toString().trim(), firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getEmail());
            reference.push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            });
            submit.setEnabled(true);
        }
    }
}
