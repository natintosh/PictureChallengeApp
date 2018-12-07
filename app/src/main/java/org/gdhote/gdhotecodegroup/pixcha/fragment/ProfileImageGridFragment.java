package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileGridAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;
import org.gdhote.gdhotecodegroup.pixcha.model.User;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;

import java.util.ArrayList;
import java.util.List;

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
    private ProfileGridAdapter gridListAdapter;
    private List<FeedPost> feedList;

    private static String ARG_USER = "user";

    public static ProfileImageGridFragment newInstance(User user) {
        ProfileImageGridFragment fragment = new ProfileImageGridFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_image_grid, container, false);

        mRecyclerView = view.findViewById(R.id.profile_grid_rv);
        gridListAdapter = new ProfileGridAdapter(this, getContext());
        mRecyclerView.setAdapter(gridListAdapter);

        fetchDataIntoGridAdapter();

        return view;
    }

    private void fetchDataIntoGridAdapter() {
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

                gridListAdapter.setDataSet(feedList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "failed to update list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataIntoGridAdapter();
    }

    @Override
    public void onListItemClick(int position) {
        ProfileFragment.getProfileViewPager().setCurrentItem(1, true);
        ProfileImageListFragment.mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onListItemLongClick(int position, Bitmap bitmap) {
        View imageDialog = getLayoutInflater().inflate(R.layout.dialog_custom_image, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        SquareImageView imageView = imageDialog.findViewById(R.id.custom_image_dialog_image);
        imageView.setImageBitmap(bitmap);
        builder.setView(imageDialog);
        builder.show();
    }
}
