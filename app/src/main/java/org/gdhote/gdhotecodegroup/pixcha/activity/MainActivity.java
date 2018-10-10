package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.gdhote.gdhotecodegroup.pixcha.fragment.FeedsFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileFragment;
import org.gdhote.gdhotecodegroup.pixcha.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    FragmentManager mFragmentManager;
    private final FeedsFragment mFeedsFragment = new FeedsFragment();
    private final ProfileFragment mProfileFragment = new ProfileFragment();
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();

        activeFragment = mFeedsFragment;
        loadFragment(mFeedsFragment);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count != 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.view_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();

                    switch (itemId) {
                        case R.id.nav_action_feeds:
                            if (activeFragment != mFeedsFragment) {
                                Toast.makeText(MainActivity.this, "Feeds", Toast.LENGTH_SHORT).show();
                                loadFragment(mFeedsFragment);
                                activeFragment = mFeedsFragment;
                            }
                            return true;
                        case R.id.nav_action_camera:
                            Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.nav_action_profile:
                            if (activeFragment != mProfileFragment) {
                                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                                loadFragment(mProfileFragment);
                                activeFragment = mProfileFragment;
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            };


}
