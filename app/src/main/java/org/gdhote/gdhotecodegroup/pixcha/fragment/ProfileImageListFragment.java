package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.os.Bundle;
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
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;
import org.gdhote.gdhotecodegroup.pixcha.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileImageListFragment extends Fragment {

    private final static String TAG = ProfileImageListFragment.class.getSimpleName();

    public ProfileImageListFragment() {
        // Required empty public constructor
    }

    public static RecyclerView mRecyclerView;
    private List<FeedPost> feedList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image_list, container, false);


        mRecyclerView = view.findViewById(R.id.profile_list_rv);
        final ProfileListAdapter listAdapter = new ProfileListAdapter(getContext());
        mRecyclerView.setAdapter(listAdapter);

        User user = CurrentUser.getInstance();
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        CollectionReference colRef = firestoreDb.collection("uploads");

        colRef.whereEqualTo("uploadedBy", user.getId()).orderBy("uploadedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) return;

                        feedList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            feedList.add(documentSnapshot.toObject(FeedPost.class));
                        }

                        listAdapter.setDataSet(feedList);
                    }
                });

        return view;
    }
}
