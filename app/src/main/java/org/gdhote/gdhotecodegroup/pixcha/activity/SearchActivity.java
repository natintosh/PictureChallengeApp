package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchActivity extends AppCompatActivity implements SearchListAdapter.OnSearchItemListener {

    private RecyclerView mRecyclerView;
    private SearchListAdapter searchListAdapter;
    private List<User> queryResults;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        noResultTextView = findViewById(R.id.activity_search_no_result_text);
        swipeRefreshLayout = findViewById(R.id.search_activity_swipe_refresh);
        mRecyclerView = findViewById(R.id.search_recycler_view);
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
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            fetchAndUpdateAdapter(query);
        }
    }

    private void fetchAndUpdateAdapter(final String query) {

        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        CollectionReference usersColRef = firestoreDb.collection("users");

        usersColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                queryResults = new ArrayList<>();
                if (task.isSuccessful() || !task.getResult().isEmpty()) {

                    for (DocumentSnapshot snapshot : task.getResult()) {
                        User user = snapshot.toObject(User.class);
                        String userNameLowerCased = (user.getDisplayName() != null || !user.getDisplayName().isEmpty()
                                ? user.getDisplayName().toLowerCase() : "");

                        if (userNameLowerCased.contains(query.toLowerCase()) && !userNameLowerCased.isEmpty() && userNameLowerCased != null) {
                            queryResults.add(user);
                        }
                    }
                    if (queryResults.isEmpty()) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        noResultTextView.setVisibility(View.VISIBLE);
                    }else {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        noResultTextView.setVisibility(View.GONE);
                    }
                    searchListAdapter.setDataSet(queryResults);
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_fragment_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.feed_fragment_search_action_menu).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return super.onCreateOptionsMenu(menu);
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
