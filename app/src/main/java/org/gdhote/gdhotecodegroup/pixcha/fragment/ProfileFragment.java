package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileViewPagerAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.ui.CircularImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private static ViewPager mProfileViewPager;
    private PagerAdapter mPagerAdapter;

    private OnProfileDetailsEditButtonListener mEditButtonCallback;
    private OnSignOutMenuClickListener mSignOutCallback;


    public interface OnProfileDetailsEditButtonListener {
        void onProfileDetailsEditButtonClick();
    }

    public interface OnSignOutMenuClickListener {
        void onSignOutMenuClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mEditButtonCallback = (OnProfileDetailsEditButtonListener) context;
            mSignOutCallback = (OnSignOutMenuClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditButtonCallback = null;
        mSignOutCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);
        MainActivity.activeFragment = this;
        MainActivity.isAwayFromNav = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);
        MainActivity.activeFragment = this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_profile, container, false);
        setNavigationViewVisibility(true);
        MainActivity.activeFragment = this;
        MainActivity.isAwayFromNav = false;

        CurrentUser user = CurrentUser.getInstance();
        getActivity().setTitle(user.getDisplayName());
        getProfileViewPager();

        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        CollectionReference userColRef = firestoreDb.collection("users");
        DocumentReference userDocRef = userColRef.document(user.getId());
        CollectionReference userUploadsColRef = userDocRef.collection("uploads");

        TextView bioText = view.findViewById(R.id.profile_detail_bio_tv);
        final TextView postText = view.findViewById(R.id.profile_details_number_of_post_tv);
        CircularImageView profileImage = view.findViewById(R.id.profile_details_profile_image);
        FloatingActionButton editProfileButton = view.findViewById(R.id.profile_details_edit_profile_btn);

        bioText.setText(user.getBio());
        GlideApp.with(this)
                .asBitmap()
                .load(user.getProfileImageUrl())
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .into(profileImage);

        userUploadsColRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) return;

                int number = queryDocumentSnapshots.size();

                postText.setText(Integer.toString(number));
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileFragment();
            }
        });

        initialiseViewPager(view);
        return view;
    }

    private void openEditProfileFragment() {
        if (mEditButtonCallback != null) {
            mEditButtonCallback.onProfileDetailsEditButtonClick();
        }
    }

    private void signOut() {
        if (mSignOutCallback != null) {
            mSignOutCallback.onSignOutMenuClick();
            ;
        }
    }


    private void setNavigationViewVisibility(Boolean setVisibility) {

        if (setVisibility) {
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } else {
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void initialiseViewPager(View view) {
        final ImageButton gridButton = view.findViewById(R.id.profile_grid_img_btn);
        final ImageButton listButton = view.findViewById(R.id.profile_list_img_btn);

        mPagerAdapter = new ProfileViewPagerAdapter(getChildFragmentManager());
        mProfileViewPager = view.findViewById(R.id.profile_view_pager);
        mProfileViewPager.setAdapter(mPagerAdapter);

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileViewPager.setCurrentItem(0, true);
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileViewPager.setCurrentItem(1, true);
            }
        });

        int currentPosition = mProfileViewPager.getCurrentItem();

        if (currentPosition == 0) {
            gridButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
            listButton.setColorFilter(Color.BLACK);
        } else {
            listButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
            gridButton.setColorFilter(Color.BLACK);
        }


        mProfileViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_picture_grid, null);

                switch (position) {
                    case 0:
                        gridButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
                        listButton.setColorFilter(Color.BLACK);
                        break;
                    case 1:
                        listButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
                        gridButton.setColorFilter(Color.BLACK);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static ViewPager getProfileViewPager() {
        return mProfileViewPager;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.profile_details_sign_out_menu:
                signOut();
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
