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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileViewPagerAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.User;
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

    private SetProfileFragmentListeners mProfileFragmentCallback;

    private static final String ARG_USER_ID = "user-id";
    private static final String ARG_FRAGMENT_TYPE = "fragment-type";

    public interface SetProfileFragmentListeners {
        void onProfileDetailsEditButtonClick();

        void onSignOutMenuClick();
    }


    public static ProfileFragment newInstance(User user, int type) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, user);
        args.putInt(ARG_FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    private User user;
    private int fragmentType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER_ID);
            fragmentType = getArguments().getInt(ARG_FRAGMENT_TYPE, 0);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mProfileFragmentCallback = (SetProfileFragmentListeners) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProfileFragmentCallback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fragmentType == 0) {
            MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);
            MainActivity.activeFragment = this;
        }
        final User user = getUser();
        updateProfileView(user);
    }

    private User getUser() {
        final User[] user = new User[1];

        switch (fragmentType) {
            case 0:
                user[0] = CurrentUser.getInstance();
                editProfileButton.show();
                break;
            case 1:
                editProfileButton.hide();
                user[0] = this.user;
                break;
            default:
                user[0] = CurrentUser.getInstance();
                break;

        }
        return user[0];
    }

    private void updateProfileView(User user) {
        getActivity().setTitle(user.getDisplayName());
        bioText.setText(user.getBio());
        GlideApp.with(this)
                .asBitmap()
                .load(user.getProfileImageUrl())
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .into(profileImage);

        FirebaseFirestore.getInstance().collection("uploads")
                .orderBy("uploadedAt", Query.Direction.DESCENDING)
                .whereEqualTo("uploadedBy", user.getId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) return;

                        String size = String.valueOf(queryDocumentSnapshots.size());

                        postText.setText(size);
                    }
                });
    }

    private TextView bioText;
    private TextView postText;
    private CircularImageView profileImage;
    private FloatingActionButton editProfileButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_profile, container, false);
        if (fragmentType == 0) {
            setNavigationViewVisibility(true);
            MainActivity.activeFragment = this;
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initialiseViewPager(view);

        updateProfileView(getUser());

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileFragment();
            }
        });


        return view;
    }

    private void openEditProfileFragment() {
        if (mProfileFragmentCallback != null) {
            mProfileFragmentCallback.onProfileDetailsEditButtonClick();
        }
    }

    private void signOut() {
        if (mProfileFragmentCallback != null) {
            mProfileFragmentCallback.onSignOutMenuClick();
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
        bioText = view.findViewById(R.id.profile_detail_bio_tv);
        postText = view.findViewById(R.id.profile_details_number_of_post_tv);
        profileImage = view.findViewById(R.id.profile_details_profile_image);
        editProfileButton = view.findViewById(R.id.profile_details_edit_profile_btn);

        final ImageButton gridButton = view.findViewById(R.id.profile_grid_img_btn);
        final ImageButton listButton = view.findViewById(R.id.profile_list_img_btn);

        PagerAdapter mPagerAdapter = new ProfileViewPagerAdapter(getChildFragmentManager(), user);
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

    static ViewPager getProfileViewPager() {
        return mProfileViewPager;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (fragmentType == 0) {
            inflater.inflate(R.menu.profile_details_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (fragmentType == 0) {
            final int id = item.getItemId();
            switch (id) {
                case R.id.profile_details_sign_out_menu:
                    signOut();
                default:
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
