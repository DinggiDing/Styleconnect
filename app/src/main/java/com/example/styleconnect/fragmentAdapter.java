package com.example.styleconnect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class fragmentAdapter extends FragmentPagerAdapter {

    int num;

    public fragmentAdapter(FragmentManager fm, int num) {
        super(fm);
        this.num = num;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragment_tab1.newInstance();
            case 1:
                return fragment_tab2.newInstance();
            case 2:
                return fragment_tab3.newInstance();
            case 3:
                return fragment_tab4.newInstance();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num;
    }
}
