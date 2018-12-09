package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.adapter.CommentListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.adapter.ProfileListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.Comment;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;
import org.gdhote.gdhotecodegroup.pixcha.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ProfileImageListFragment extends Fragment implements ProfileListAdapter.SetFeedListItemCallback {

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
        listAdapter = new ProfileListAdapter(getContext(), this);
        mRecyclerView.setAdapter(listAdapter);

        fetchDataIntoListAdapter();

        return view;
    }

    private void fetchDataIntoListAdapter() {
        User user = (this.user != null ? this.user : CurrentUser.getInstance());
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        CollectionReference colRef = firestoreDb.collection("uploads");
        Query query = colRef.orderBy("uploadedAt", Query.Direction.DESCENDING).whereEqualTo("uploadedBy", user.getId());


        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataIntoListAdapter();
    }

    @Override
    public void onSquareImageLongPress(String postId) {
        final Dialog dialogBuilder = new Dialog(getContext());
        View dialogView = dialogBuilder.getLayoutInflater().inflate(R.layout.dialog_comments, null);

        RecyclerView commentRecyclerView = dialogView.findViewById(R.id.comment_dialog_recycler_view);
        final CommentListAdapter commentListAdapter = new CommentListAdapter(getContext());
        commentRecyclerView.setAdapter(commentListAdapter);
        final SwipeRefreshLayout refreshLayout = dialogView.findViewById(R.id.comment_dialog_swipe_refresh);
        refreshLayout.setRefreshing(true);

        final CollectionReference commentColRef = FirebaseFirestore.getInstance().collection("uploads").document(postId)
                .collection("comments");

        updateComments(commentColRef, commentListAdapter, refreshLayout);

        final EditText commentMsg = dialogView.findViewById(R.id.comment_dialog_edit_text);
        FloatingActionButton sendButton = dialogView.findViewById(R.id.comment_dialog_send_fab);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = commentMsg.getText().toString();

                if (!msg.isEmpty()) {
                    User user = CurrentUser.getInstance();

                    Comment comment = new Comment(user.getDisplayName(), msg, Timestamp.now());
                    commentColRef.add(comment);

                    commentMsg.getText().clear();
                }
            }
        });

        dialogBuilder.setContentView(dialogView);
        dialogBuilder.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialogBuilder.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowHeight = (int) (displayHeight * 0.9f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        dialogBuilder.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onPopUpMenuPress(User user, final FeedPost post, ImageButton popUpButton) {
        final FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        final CollectionReference uploadscolRef = firestoreDb.collection("uploads");
        final DocumentReference feedDocRef = uploadscolRef.document(post.getId());
        final CollectionReference likesColRef = feedDocRef.collection("likes");
        final CollectionReference commentsColRef = feedDocRef.collection("comments");

        PopupMenu popupMenu = new PopupMenu(getContext(), popUpButton);
        popupMenu.getMenuInflater().inflate(R.menu.feeds_popup_menu, popupMenu.getMenu());
        if (user != null) {
            if (!user.getId().equals(CurrentUser.getInstance().getId())) {
                MenuItem item = popupMenu.getMenu().findItem(R.id.feed_popup_more_menu);
                if (item != null) {
                    item.setVisible(false);
                }
            }
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.feed_popup_report_action_menu:
                            final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                            View dialogView = dialogBuilder.getLayoutInflater().inflate(R.layout.dialog_custom_report, null);

                            final EditText reportEditText = dialogView.findViewById(R.id.report_dialog_edit_text);
                            Button cancelButton = dialogView.findViewById(R.id.report_dialog_cancel_btn);
                            Button submitButton = dialogView.findViewById(R.id.report_dialog_submit_btn);

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBuilder.dismiss();
                                }
                            });

                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Map<String, Object> reportMap = new HashMap<>();
                                    reportMap.put("reportedBy", CurrentUser.getInstance().getId());
                                    reportMap.put("reportedPost", post.getId());
                                    reportMap.put("reportedText", reportEditText.getText().toString());

                                    firestoreDb.collection("reports").document()
                                            .set(reportMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Reported", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialogBuilder.dismiss();
                                }
                            });

                            dialogBuilder.setView(dialogView);
                            dialogBuilder.show();
                            break;
                        case R.id.feed_popup_delete_action_menu:
                            final WriteBatch batch = firestoreDb.batch();

                            StorageReference rootStorageReference = FirebaseStorage.getInstance().getReference();
                            StorageReference profileImagesStorageRef = rootStorageReference.child("uploaded_images");
                            String fileName = "image" + "_" + post.getId() + ".jpg";
                            final StorageReference fileRef = profileImagesStorageRef.child(fileName);

                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    feedDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            likesColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (task.isSuccessful() || !task.getResult().isEmpty()) {
                                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                                            batch.delete(snapshot.getReference());
                                                        }
                                                    }

                                                    commentsColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if (task.isSuccessful() || !task.getResult().isEmpty()) {
                                                                for (DocumentSnapshot snapshot : task.getResult()) {
                                                                    batch.delete(snapshot.getReference());
                                                                }
                                                            }

                                                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Unknown error occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private List<Comment> commentList;

    private void updateComments(final CollectionReference commentColRef, final CommentListAdapter commentListAdapter, final SwipeRefreshLayout refreshLayout) {
        commentColRef.orderBy("uploadedAt", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) return;

                commentList = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    commentList.add(documentSnapshot.toObject(Comment.class));
                }
                commentListAdapter.setDataSet(commentList);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
            }
        });
    }
}
