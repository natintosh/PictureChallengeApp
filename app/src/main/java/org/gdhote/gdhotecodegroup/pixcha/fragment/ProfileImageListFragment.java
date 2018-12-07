package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

    static RecyclerView mRecyclerView;
    private ProfileListAdapter listAdapter;
    private List<FeedPost> feedList;

    private static String ARG_USER = "user";

    public static ProfileImageListFragment newInstance(User user) {
        ProfileImageListFragment fragment = new ProfileImageListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image_list, container, false);


        mRecyclerView = view.findViewById(R.id.profile_list_rv);
        listAdapter = new ProfileListAdapter(getContext());
        mRecyclerView.setAdapter(listAdapter);

        fetchDataIntoListAdapter();

        return view;
    }

    private void fetchDataIntoListAdapter() {
        User user = (this.user != null ? this.user : CurrentUser.getInstance());
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        CollectionReference colRef = firestoreDb.collection("uploads");
        Query query = colRef.orderBy("uploadedAt", Query.Direction.DESCENDING).whereEqualTo("uploadedBy", user.getId());


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                feedList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    feedList.add(documentSnapshot.toObject(FeedPost.class));
                }
                listAdapter.setDataSet(feedList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "failed to update list", Toast.LENGTH_SHORT).show();
                Log.d(ProfileImageListFragment.class.getSimpleName(), e.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataIntoListAdapter();
    }

}
