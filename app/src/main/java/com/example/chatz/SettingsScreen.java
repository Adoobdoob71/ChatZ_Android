package com.example.chatz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.appbar.MaterialToolbar;

public class SettingsScreen extends AppCompatActivity {

    MaterialToolbar toolbar;
    Switch themeSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme));
        setContentView(R.layout.activity_settings_screen);
        toolbar = findViewById(R.id.Toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        themeSwitch = findViewById(R.id.ThemeSwitch);
        themeSwitch.setChecked(getSharedPreferences("theme", 0).getInt("theme", R.style.AppTheme) == R.style.AppTheme_Dark);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("theme", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked)
                    editor.putInt("theme", R.style.AppTheme_Dark);
                else
                    editor.putInt("theme", R.style.AppTheme);
                editor.apply();
                TaskStackBuilder.create(getApplicationContext())
                        .addNextIntent(new Intent(getApplicationContext(), MainActivity.class))
                        .addNextIntent(getIntent())
                        .startActivities();
            }
        });
    }
}
