package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gdhote.gdhotecodegroup.pixcha.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileListAdapterViewHolder> {
    @NonNull
    @Override
    public ProfileListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_list_item, parent, false);
        return new ProfileListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileListAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ProfileListAdapterViewHolder extends RecyclerView.ViewHolder {

        public ProfileListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
