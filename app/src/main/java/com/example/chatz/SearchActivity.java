package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.chatz.Adapters.GroupAdapter;
import com.example.chatz.Classes.Group;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ListView listView;
    GroupAdapter groupAdapter;
    ConstraintLayout constraintLayout;
    BottomSheetBehavior bottomSheetBehavior;
    ArrayList<Group> arrayList;
    EditText searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_search);
        searchText = findViewById(R.id.SearchEditText);
        listView = findViewById(R.id.SearchBarListView);
        constraintLayout = findViewById(R.id.BottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        arrayList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, arrayList);
        listView.setAdapter(groupAdapter);
        final DatabaseReference reference = firebaseDatabase.getReference("groups");
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchText.getText().toString().trim().length() != 0)
                    reference.orderByKey().startAt(searchText.getText().toString()).endAt(searchText.getText().toString() + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            arrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Group group = dataSnapshot.getValue(Group.class);
                                group.setName(dataSnapshot.getKey());
                                arrayList.add(group);
                            }
                            groupAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });
    }

    public void Finish(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
