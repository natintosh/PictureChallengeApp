package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gdhote.gdhotecodegroup.pixcha.adapter.FeedsListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsFragment extends Fragment {

    //
    public FeedsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_feeds_list,  container, false);

        RecyclerView feedsRecyclerView = view.findViewById(R.id.feeds_list);
        FeedsListAdapter feedsListAdapter = new FeedsListAdapter();

        feedsRecyclerView.setAdapter(feedsListAdapter);
        return view;
    }
}
