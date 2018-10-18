package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileGridAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileImageGridFragment extends Fragment implements ProfileGridAdapter.ListItemClickListener {

    public ProfileImageGridFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    ProfileGridAdapter mGridListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image_grid, container, false);

        mRecyclerView = view.findViewById(R.id.profile_grid_rv);
        mGridListAdapter = new ProfileGridAdapter(this);
        mRecyclerView.setAdapter(mGridListAdapter);

        return view;
    }

    @Override
    public void onListItemClick(int position) {
        ProfileFragment.getProfileViewPager().setCurrentItem(1, true);
        ProfileImageListFragment.mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onListItemLongClick(int position) {
        View imageDialog = getLayoutInflater().inflate(R.layout.custom_image_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(imageDialog);
        builder.show();
    }
}
