package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileGridAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileImageGridFragment extends Fragment {

    public ProfileImageGridFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image_grid, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.profile_grid_rv);
        ProfileGridAdapter gridAdapter = new ProfileGridAdapter();
        recyclerView.setAdapter(gridAdapter);

        return view;
    }
}
