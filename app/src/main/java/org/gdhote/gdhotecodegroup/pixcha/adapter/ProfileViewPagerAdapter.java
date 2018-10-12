package org.gdhote.gdhotecodegroup.pixcha.adapter;

import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileImageGridFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileImageListFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ProfileViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 2;

    public ProfileViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ProfileImageGridFragment();
            case 1:
                return new ProfileImageListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
