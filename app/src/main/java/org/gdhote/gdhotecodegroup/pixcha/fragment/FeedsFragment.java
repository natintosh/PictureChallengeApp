package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.adapter.FeedsListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsFragment extends Fragment {

    private static final String TAG = FeedsFragment.class.getSimpleName();

    public FeedsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.activeFragment = this;
        MainActivity.bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.activeFragment = this;
        MainActivity.bottomNavigationView.getMenu().getItem(0).setChecked(true);

        feedsListAdapter.setDataSet(feedList);

    }


    private List<FeedPost> feedList;
    private FeedsListAdapter feedsListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_feeds_list, container, false);

        getActivity().setTitle(getResources().getString(R.string.app_name));
        setNavigationViewVisibility(true);
        MainActivity.activeFragment = this;

        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        CollectionReference colRef = firestoreDb.collection("uploads");

        RecyclerView feedsRecyclerView = view.findViewById(R.id.feeds_list);
        feedsListAdapter = new FeedsListAdapter(getContext());
        feedsRecyclerView.setAdapter(feedsListAdapter);

        colRef.orderBy("uploadedAt", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                feedList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Log.i(TAG, ((FeedPost) documentSnapshot.toObject(FeedPost.class)).getId());
                    feedList.add(documentSnapshot.toObject(FeedPost.class));
                }
                feedsListAdapter.setDataSet(feedList);
            }
        });

        return view;
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
}
