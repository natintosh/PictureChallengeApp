package org.gdhote.gdhotecodegroup.pixcha;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    FragmentManager mFragmentManager;
    FeedsFragment mFeedsFragment;
    ProfileFragment mProfileFragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();

                    switch (itemId) {
                        case R.id.nav_action_feeds:
                            Toast.makeText(MainActivity.this, "Feeds", Toast.LENGTH_SHORT).show();
                            mFeedsFragment = new FeedsFragment();
                            loadFragment(mFeedsFragment);
                            return true;
                        case R.id.nav_action_camera:
                            Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.nav_action_profile:
                            Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                            mProfileFragment = new ProfileFragment();
                            loadFragment(mProfileFragment);
                            return true;
                        default:
                            return false;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mProfileFragment = new ProfileFragment();
        mFeedsFragment = new FeedsFragment();

        loadFragment(mFeedsFragment);


    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.view_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
