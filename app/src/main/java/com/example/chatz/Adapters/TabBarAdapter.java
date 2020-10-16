package com.example.chatz.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatz.GroupsScreen;
import com.example.chatz.HomeScreen;

public class TabBarAdapter extends FragmentPagerAdapter {

    public TabBarAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        HomeScreen homeScreen = new HomeScreen();
        GroupsScreen groupsScreen = new GroupsScreen();
        switch (position) {
            case 0:
                return homeScreen;
            case 1:
                return groupsScreen;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = getItem(position).getClass().getSimpleName();
        return title.subSequence(0, title.indexOf("S"));
    }
}
