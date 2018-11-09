package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.fragment.FeedsFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileFragment;
import org.gdhote.gdhotecodegroup.pixcha.model.MainActivityNavigationViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {

    private final int FEED_POSTION = 0;
    private final int CAMERA_POSITION = 1;
    private final int PROFILE_POSITION = 2;

    private FragmentManager mFragmentManager;
    private final FeedsFragment mFeedsFragment = new FeedsFragment();
    private final ProfileFragment mProfileFragment = new ProfileFragment();
    private Fragment activeFragment;
    private MainActivityNavigationViewModel navigationViewModel;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationViewModel = ViewModelProviders.of(this).get(MainActivityNavigationViewModel.class);

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();
        activeFragment = mFeedsFragment;
        loadFragment(mFeedsFragment);
    }

    @Override
    public void onBackPressed() {
        int selectedNavItemId = bottomNavigationView.getSelectedItemId();

        if (selectedNavItemId != R.id.nav_action_feeds) {
            super.onBackPressed();
        } else {
            super.finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        int selectedItemId = bottomNavigationView.getMenu().getItem(navigationViewModel.getPosition()).getItemId();
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.view_frame_layout, fragment);

        final int count = mFragmentManager.getBackStackEntryCount();
        transaction.addToBackStack(null);

        transaction.commit();

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if (mFragmentManager.getBackStackEntryCount() <= count) {
                    // pop all the fragment and remove the listener
                    mFragmentManager.popBackStack(PROFILE_POSITION, POP_BACK_STACK_INCLUSIVE);
                    mFragmentManager.removeOnBackStackChangedListener(this);
                    // set the home button selected
                    bottomNavigationView.getMenu().getItem(FEED_POSTION).setChecked(true);
                }
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();

                    switch (itemId) {
                        case R.id.nav_action_feeds:
                            if (activeFragment != mFeedsFragment) {
                                loadFragment(mFeedsFragment);
                                activeFragment = mFeedsFragment;
                                navigationViewModel.setPosition(FEED_POSTION);
                            }
                            return true;
                        case R.id.nav_action_camera:
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.nav_action_profile:
                            if (activeFragment != mProfileFragment) {
                                loadFragment(mProfileFragment);
                                activeFragment = mProfileFragment;
                                navigationViewModel.setPosition(PROFILE_POSITION);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            };


}
