package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.LikesActivity;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.adapter.CommentListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.adapter.FeedsListAdapter;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FeedsFragment extends Fragment implements FeedsListAdapter.SetFeedListItemCallback {

    private static final String TAG = FeedsFragment.class.getSimpleName();

    public FeedsFragment() {
        // Required empty public constructor
    }

    private static RecyclerView feedsRecyclerView;
    private static List<FeedPost> feedList;
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


        feedsRecyclerView = view.findViewById(R.id.feeds_list);
        feedSwipeRefreshLayout = view.findViewById(R.id.feed_swipe_refresh);
        feedsListAdapter = new FeedsListAdapter(getContext(), this);
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

    private static List<FeedPost> getFeeds() {
        return feedList;
    }

    public static void scrollToTop() {
        if (!getFeeds().isEmpty() && getFeeds() != null) {
            feedsRecyclerView.smoothScrollToPosition(0);
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
    public void onSeeLikeClickListener(String postId) {

        Intent intent = new Intent(getContext(), LikesActivity.class);
        intent.putExtra(LikesActivity.POST_ID_INTENT_EXTRA, postId);
        startActivity(intent);
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
