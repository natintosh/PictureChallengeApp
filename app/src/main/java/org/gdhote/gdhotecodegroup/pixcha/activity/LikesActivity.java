package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.adapter.SearchListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class LikesActivity extends AppCompatActivity implements SearchListAdapter.OnSearchItemListener {

    public static final String POST_ID_INTENT_EXTRA = "post-id";
    private RecyclerView mRecyclerView;
    private SearchListAdapter searchListAdapter;
    private List<User> queryResults;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Liked by");

        swipeRefreshLayout = findViewById(R.id.like_activity_swipe_refresh);
        mRecyclerView = findViewById(R.id.like_recycler_view);
        searchListAdapter = new SearchListAdapter(this, this);
        mRecyclerView.setAdapter(searchListAdapter);

        swipeRefreshLayout.setRefreshing(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        swipeRefreshLayout.setRefreshing(true);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(POST_ID_INTENT_EXTRA)) {
            String query = intent.getStringExtra(POST_ID_INTENT_EXTRA);

            fetchAndUpdateAdapter(query);
        }
    }


    private void fetchAndUpdateAdapter(final String query) {

        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        final CollectionReference usersColRef = firestoreDb.collection("users");
        CollectionReference uploadsColRef = firestoreDb.collection("uploads");
        DocumentReference uploadDocRef = uploadsColRef.document(query);
        CollectionReference likesColRef = uploadDocRef.collection("likes");

        queryResults = new ArrayList<>();
        likesColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful() || !task.getResult().isEmpty()) {

                    for (DocumentSnapshot snapshot : task.getResult()) {

                        String userId = snapshot.getId();

                        usersColRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                queryResults.add(user);
                                searchListAdapter.setDataSet(queryResults);
                            }
                        });
                    }

                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);

                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onItemClickListener(int position) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_INTENT_EXTRA, queryResults.get(position).getId());
        startActivity(intent);
    }
}
