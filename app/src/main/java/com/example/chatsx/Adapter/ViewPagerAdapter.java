package com.example.chatsx.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatsx.Fragment.CallsFragment;
import com.example.chatsx.Fragment.ChatsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String[] titles = new String[]{"Chats", "Calls"};

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new CallsFragment();
        }
        return new ChatsFragment();
    }

    @Override
    public int getItemCount() {
        //no. of fragments or tabs
        return titles.length;
    }
}
