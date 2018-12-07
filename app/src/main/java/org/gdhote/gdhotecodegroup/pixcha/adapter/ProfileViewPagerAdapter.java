package org.gdhote.gdhotecodegroup.pixcha.adapter;

import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileImageGridFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileImageListFragment;
import org.gdhote.gdhotecodegroup.pixcha.model.User;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ProfileViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 2;

    private final ProfileImageGridFragment profileImageGridFragment;

    private final ProfileImageListFragment profileImageListFragment;

    public ProfileViewPagerAdapter(FragmentManager fm, User user) {
        super(fm);
        profileImageGridFragment = ProfileImageGridFragment.newInstance(user);
        profileImageListFragment = ProfileImageListFragment.newInstance(user);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return profileImageGridFragment;
            case 1:
                return profileImageListFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
