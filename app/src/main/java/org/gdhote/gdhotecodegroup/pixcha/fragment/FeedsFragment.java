package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FeedsFragment extends Fragment {

    private static final String TAG = FeedsFragment.class.getSimpleName();

    public FeedsFragment() {
        // Required empty public constructor
    }

    private List<FeedPost> feedList;
    private FeedsListAdapter feedsListAdapter;
    private ListenerRegistration feedQueryListenerReg;
    private SwipeRefreshLayout feedSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.activeFragment = this;
        MainActivity.bottomNavigationView.getMenu().getItem(0).setChecked(true);
        fetchDataIntoAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_feeds_list, container, false);

        getActivity().setTitle(getResources().getString(R.string.app_name));
        setNavigationViewVisibility(true);
        MainActivity.activeFragment = this;


        RecyclerView feedsRecyclerView = view.findViewById(R.id.feeds_list);
        feedSwipeRefreshLayout = view.findViewById(R.id.feed_swipe_refresh);
        feedsListAdapter = new FeedsListAdapter(getContext());
        feedsRecyclerView.setAdapter(feedsListAdapter);

        feedSwipeRefreshLayout.setRefreshing(true);
        fetchDataIntoAdapter();


        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDataIntoAdapter();
            }
        });

        return view;
    }

    private void fetchDataIntoAdapter() {
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        final CollectionReference colRef = firestoreDb.collection("uploads");

        colRef.orderBy("uploadedAt", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                feedList = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    feedList.add(queryDocumentSnapshot.toObject(FeedPost.class));
                }

                feedsListAdapter.setDataSet(feedList);
                feedSwipeRefreshLayout.setRefreshing(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to refresh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdapter(CollectionReference colRef) {
        feedQueryListenerReg = colRef.orderBy("uploadedAt", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                feedList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    feedList.add(documentSnapshot.toObject(FeedPost.class));
                }
                feedsListAdapter.setDataSet(feedList);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (feedQueryListenerReg != null) {
            feedQueryListenerReg.remove();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.feed_fragment_menu, menu);


        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.feed_fragment_search_action_menu).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feed_fragment_search_action_menu:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
