package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.model.User;
import org.gdhote.gdhotecodegroup.pixcha.ui.CircularImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchListAdapterViewHolder> {

    private List<User> searchResult;
    Context context;

    private OnSearchItemListener mCallback;

    public interface OnSearchItemListener {
        void onItemClickListener(int position);
    }

    public SearchListAdapter(Context context, OnSearchItemListener listener) {
        this.mCallback = listener;
        this.context = context;
        searchResult = new ArrayList<>();
    }

    @NonNull
    @Override
    public SearchListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_result, parent, false);
        return new SearchListAdapterViewHolder(view);
    }

    public void setDataSet(List<User> dataSet) {
        searchResult = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapterViewHolder holder, int position) {
        User user = searchResult.get(position);

        GlideApp.with(context)
                .asBitmap()
                .load(user.getProfileImageUrl())
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .into(holder.profileImageView);

        holder.userNameTextView.setText(user.getDisplayName());

    }

    @Override
    public int getItemCount() {
        return searchResult.size();
    }

    public class SearchListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView userNameTextView;
        private CircularImageView profileImageView;

        public SearchListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            userNameTextView = itemView.findViewById(R.id.search_username_tv);
            profileImageView = itemView.findViewById(R.id.search_profile_image_view);
        }

        @Override
        public void onClick(View v) {
            if (mCallback == null) return;
            mCallback.onItemClickListener(getAdapterPosition());
        }
    }
}
