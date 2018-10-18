package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileViewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private static ViewPager mProfileViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_profile, container, false);
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
        return view;
    }

    public static ViewPager getProfileViewPager() {
        return mProfileViewPager;
    }

}
