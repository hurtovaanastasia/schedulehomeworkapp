package com.example.schedulehomeworkapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.schedulehomeworkapp.fragments.ScheduleFragment;
import com.example.schedulehomeworkapp.fragments.TasksFragment;
import com.example.schedulehomeworkapp.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        // default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()).commit();
        nav.setOnItemSelectedListener(item -> {
            Fragment sel = null;
            int id = item.getItemId();
            if (id==R.id.nav_schedule) sel = new ScheduleFragment();
            else if (id==R.id.nav_tasks) sel = new TasksFragment();
            else if (id==R.id.nav_settings) sel = new SettingsFragment();
            if (sel!=null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sel).commit();
            return true;
        });
    }
}
