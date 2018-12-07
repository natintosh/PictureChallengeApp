package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.fragment.FeedsFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.SignInFragment;
import org.gdhote.gdhotecodegroup.pixcha.utils.UserHelper;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.EditProfileViewModel;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.MainActivityNavigationViewModel;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.UserUploadsViewModel;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInButtonClickListener,
        ProfileFragment.SetProfileFragmentListeners {

    private static final int RC_SIGN_IN = 1001;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final int FEED_POSTION = 0;
    private final int CAMERA_POSITION = 1;
    private final int PROFILE_POSITION = 2;

    private FragmentManager mFragmentManager;
    private final FeedsFragment mFeedsFragment = new FeedsFragment();
    private final ProfileFragment mProfileFragment = new ProfileFragment();
    private final SignInFragment mSignInFragment = new SignInFragment();
    public static Fragment activeFragment;

    private MainActivityNavigationViewModel navigationViewModel;
    private EditProfileViewModel profileViewModel;
    private UserUploadsViewModel userUploadsViewModel;

    public static BottomNavigationView bottomNavigationView;

    private final static List<AuthUI.IdpConfig> signInProviders = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserHelper.updateCurrentUser();

        navigationViewModel = ViewModelProviders.of(this).get(MainActivityNavigationViewModel.class);
        profileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        profileViewModel.initialize();

        userUploadsViewModel = ViewModelProviders.of(this).get(UserUploadsViewModel.class);
        userUploadsViewModel.init();

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();
        activeFragment = mFeedsFragment;


        if (user != null) {
            loadFragment(mFeedsFragment, false);
        } else {
            startFirebaseUiIntent();
        }
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

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        // load fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.view_frame_layout, fragment);

        final int count = mFragmentManager.getBackStackEntryCount();

        if (addToBackStack) transaction.addToBackStack(null);

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

    private void setNavigationViewVisibility(Boolean setVisibility) {

        if (setVisibility) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            getSupportActionBar().hide();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();

                    switch (itemId) {
                        case R.id.nav_action_feeds:
                            if (activeFragment != mFeedsFragment) {
                                loadFragment(mFeedsFragment, true);
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
                                loadFragment(mProfileFragment, true);
                                activeFragment = mProfileFragment;
                                navigationViewModel.setPosition(PROFILE_POSITION);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                makeToast("Successfully sign in");
                FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();

                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    // The user is new, show them a fancy intro screen!
                    makeToast("Welcome");
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    intent.putExtra(EditProfileActivity.EDIT_PROFILE_TYPE_INTENT_EXTRA, EditProfileActivity.TYPE_UPDATE_PROFILE);
                    startActivity(intent);
                    setNavigationViewVisibility(false);
                } else {
                    // This is an existing user, show them a welcome back screen.
                    loadFragment(mFeedsFragment, false);
                    // Set navigation visibility
                    setNavigationViewVisibility(true);
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

                // Set navigation visibility
                setNavigationViewVisibility(false);
                loadFragment(mSignInFragment, false);
                if (response == null) {
                    makeToast("Sign in failed");
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    makeToast("No internet connection");
                    return;
                }
                makeToast("Unknown error occurred");
            }
        }
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInBottonClick() {
        startFirebaseUiIntent();
    }

    private void startFirebaseUiIntent() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(signInProviders)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.mipmap.ic_launcher_round)      // Set logo drawable
                        .setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onProfileDetailsEditButtonClick() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.EDIT_PROFILE_TYPE_INTENT_EXTRA, EditProfileActivity.TYPE_EDIT_PROFILE);
        startActivity(intent);
    }

    @Override
    public void onSignOutMenuClick() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadFragment(mSignInFragment, false);
                    }
                });
    }
}
