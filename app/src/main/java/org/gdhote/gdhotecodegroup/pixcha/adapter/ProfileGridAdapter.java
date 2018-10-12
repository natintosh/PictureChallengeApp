package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gdhote.gdhotecodegroup.pixcha.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.ProfileGridAdapterViewHolder> {
    @NonNull
    @Override
    public ProfileGridAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_grid_item, parent, false);
        return new ProfileGridAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileGridAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ProfileGridAdapterViewHolder extends RecyclerView.ViewHolder {

        public ProfileGridAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
