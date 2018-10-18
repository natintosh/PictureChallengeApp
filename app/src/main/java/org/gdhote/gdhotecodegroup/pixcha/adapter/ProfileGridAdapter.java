package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gdhote.gdhotecodegroup.pixcha.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.ProfileGridAdapterViewHolder> {

    private final ListItemClickListener onClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int position);

        void onListItemLongClick(int position);
    }

    public ProfileGridAdapter(ListItemClickListener listItemClickListener) {
        this.onClickListener = listItemClickListener;
    }

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

    public class ProfileGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ProfileGridAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onClickListener.onListItemClick(position);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            onClickListener.onListItemLongClick(position);
            return true;
        }
    }
}
