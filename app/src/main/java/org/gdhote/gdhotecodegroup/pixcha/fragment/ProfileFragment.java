package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileViewPagerAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.ui.CircularImageView;

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

    public interface OnProfileDetailsEditButtonListener {
        void onProfileDetailsEditButtonClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mEditButtonCallback = (OnProfileDetailsEditButtonListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditButtonCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);
        MainActivity.activeFragment = this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);
//        MainActivity.activeFragment = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);
        MainActivity.activeFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_profile, container, false);
        setNavigationViewVisibility(true);
        MainActivity.activeFragment = this;

        CurrentUser user = CurrentUser.getInstance();
        getActivity().setTitle(user.getDisplayName());
        getProfileViewPager();

        TextView bioText = view.findViewById(R.id.profile_detail_bio_tv);
        bioText.setText(user.getBio());

        CircularImageView profileImage = view.findViewById(R.id.profile_details_profile_image);
        Glide.with(this).asDrawable().load(user.getProfileImageUrl()).into(profileImage);
        Button editProfileButton = view.findViewById(R.id.profile_details_edit_profile_btn);
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

}
