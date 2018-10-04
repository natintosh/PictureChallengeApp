package org.gdhote.gdhotecodegroup.pixcha;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsListAdapter extends RecyclerView.Adapter<FeedsListAdapter.FeedsListAdapterViewHolder> {

    @NonNull
    @Override
    public FeedsListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feeds, parent, false);
        return new FeedsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedsListAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class FeedsListAdapterViewHolder extends RecyclerView.ViewHolder {

        public FeedsListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}