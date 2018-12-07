package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.ProfileGridAdapterViewHolder> {

    private final ListItemClickListener onClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int position);

        void onListItemLongClick(int position, Bitmap bitmap);
    }

    public ProfileGridAdapter(ListItemClickListener listItemClickListener, Context context) {
        this.onClickListener = listItemClickListener;
        this.context = context;
        feedPostList = new ArrayList<>();
    }

    private Context context;
    private List<FeedPost> feedPostList;

    public void setDataSet(List<FeedPost> feedList) {
        feedPostList = feedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileGridAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_profile_image, parent, false);
        return new ProfileGridAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileGridAdapterViewHolder holder, int position) {
        final FeedPost post = feedPostList.get(position);

        GlideApp.with(context)
                .asBitmap()
                .load(post.getImageUrl())
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .into(holder.feedImage);

    }

    @Override
    public int getItemCount() {
        return (feedPostList != null ? feedPostList.size() : 0);
    }

    public class ProfileGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        SquareImageView feedImage;

        public ProfileGridAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            feedImage = itemView.findViewById(R.id.profile_grid_list_item_image_view);

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
            SquareImageView imageView = (SquareImageView) v;
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            onClickListener.onListItemLongClick(position, bitmap);
            return true;
        }
    }
}
