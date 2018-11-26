package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.graphics.Bitmap;
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
    private List<FeedPost> feedList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image_grid, container, false);

        mRecyclerView = view.findViewById(R.id.profile_grid_rv);
        final ProfileGridAdapter gridListAdapter = new ProfileGridAdapter(this, getContext());
        mRecyclerView.setAdapter(gridListAdapter);


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

                        gridListAdapter.setDataSet(feedList);
                    }
                });

        return view;
    }

    @Override
    public void onListItemClick(int position) {
        ProfileFragment.getProfileViewPager().setCurrentItem(1, true);
        ProfileImageListFragment.mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onListItemLongClick(int position, Bitmap bitmap) {
        View imageDialog = getLayoutInflater().inflate(R.layout.custom_image_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        SquareImageView imageView = imageDialog.findViewById(R.id.custom_image_dialog_image);
        imageView.setImageBitmap(bitmap);
        builder.setView(imageDialog);
        builder.show();
    }
}
