package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailCallback;
import com.zomato.photofilters.utils.ThumbnailItem;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilterThumbnailListAdapter extends RecyclerView.Adapter<FilterThumbnailListAdapter.FilterThumbnailListAdapterViewHolder> {

    private List<ThumbnailItem> thumbs;

    final ThumbnailCallback mCallback;
    private int lastPostion = -1;

    public FilterThumbnailListAdapter(ThumbnailCallback thumbnailCallback) {
        this.mCallback = thumbnailCallback;
    }

    public void setThumbs(List<ThumbnailItem> thumbnailItems) {
        this.thumbs = thumbnailItems;
    }

    @NonNull
    @Override
    public FilterThumbnailListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_thumbnail_list_item, parent, false);

        return new FilterThumbnailListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterThumbnailListAdapterViewHolder holder, int position) {
        ThumbnailItem currentThumb = thumbs.get(position);


        holder.filterImageNameTextView.setText(currentThumb.filterName);
        holder.filterImageThumbnailImageView.setImageBitmap(currentThumb.image);
    }

    @Override
    public int getItemCount() {
        return thumbs.size();
    }

    public class FilterThumbnailListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView filterImageNameTextView;
        SquareImageView filterImageThumbnailImageView;

        public FilterThumbnailListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            filterImageNameTextView = itemView.findViewById(R.id.filter_image_thumbnail_text);
            filterImageThumbnailImageView = itemView.findViewById(R.id.filter_image_thumbnail_image);
        }

        @Override
        public void onClick(View v) {
            if (lastPostion != getAdapterPosition()) {
                Filter filter = thumbs.get(getAdapterPosition()).filter;
                mCallback.onThumbnailClick(filter);
                lastPostion = getAdapterPosition();
            }
        }
    }
}
