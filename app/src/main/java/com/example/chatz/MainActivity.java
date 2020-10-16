package com.example.chatz;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import com.example.chatz.Adapters.TabBarAdapter;
import com.example.chatz.Classes.User;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ViewPager viewPager;
    TabBarAdapter tabBarAdapter;
    TabLayout tabLayout;
    ConstraintLayout constraintLayout;
    BottomSheetBehavior bottomSheetBehavior;
    ImageButton drawerButton;
    LinearLayout searchBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout drawerHeader;
    ImageButton contactsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.ViewPager);
        tabBarAdapter = new TabBarAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabBarAdapter);
        tabLayout = findViewById(R.id.TabLayout);
        searchBar = findViewById(R.id.SearchBar);
        tabLayout.setupWithViewPager(viewPager);
        drawerLayout = findViewById(R.id.DrawerLayout);
        navigationView = findViewById(R.id.NavigationView);
        drawerHeader = (LinearLayout) navigationView.getHeaderView(0);
        constraintLayout = findViewById(R.id.BottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        drawerButton = findViewById(R.id.DrawerButton);
        contactsButton = findViewById(R.id.ContactsButton);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (firebaseAuth.getCurrentUser() != null)
                startActivity(new Intent(getApplicationContext(), ContactScreen.class));
            else
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        contactsButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                return true;
            }
        });
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null)
                    startActivity(new Intent(getApplicationContext(), Register.class));
                else {
                    Intent intent = new Intent(getApplicationContext(), ProfileScreen.class);
                    intent.putExtra("userUID", firebaseAuth.getCurrentUser().getUid());
                    startActivity(intent);
                }
            }
        });
        drawerHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.MyProfile:
                        if (firebaseAuth.getCurrentUser() != null) {
                            Intent intent = new Intent(getApplicationContext(), ProfileScreen.class);
                            intent.putExtra("userUID", firebaseAuth.getCurrentUser().getUid());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), Register.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.Settings:
                        startActivity(new Intent(getApplicationContext(), SettingsScreen.class));
                        break;
                }
                return true;
            }
        });
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                LoadHeader();
            }
        });
    }

    public void LoadHeader() {
        final ImageView profilePic = drawerHeader.findViewById(R.id.ProfilePicture);
        final TextView username = drawerHeader.findViewById(R.id.Username);
        final TextView email = drawerHeader.findViewById(R.id.Email);
        final TextView description = drawerHeader.findViewById(R.id.Description);
        if (firebaseAuth.getCurrentUser() != null) {
            DatabaseReference reference = firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfilePictureUrl()).into(profilePic);
                        username.setText(user.getUsername());
                        email.setText(firebaseAuth.getCurrentUser().getEmail());
                        description.setText(user.getDescription());
                    }catch (Exception error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
