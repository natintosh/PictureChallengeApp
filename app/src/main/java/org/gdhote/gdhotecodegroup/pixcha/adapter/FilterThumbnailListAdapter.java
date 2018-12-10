package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailCallback;
import com.zomato.photofilters.utils.ThumbnailItem;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilterThumbnailListAdapter extends RecyclerView.Adapter<FilterThumbnailListAdapter.FilterThumbnailListAdapterViewHolder> {

    private List<ThumbnailItem> thumbs;
    private Context context;

    final ThumbnailCallback mCallback;
    private int lastPostion = -1;

    public FilterThumbnailListAdapter(ThumbnailCallback thumbnailCallback, Context context) {
        this.mCallback = thumbnailCallback;
        this.context = context;
    }

    public void setThumbs(List<ThumbnailItem> thumbnailItems) {
        this.thumbs = thumbnailItems;
    }

    @NonNull
    @Override
    public FilterThumbnailListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_thumbnail, parent, false);

        return new FilterThumbnailListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterThumbnailListAdapterViewHolder holder, int position) {
        ThumbnailItem currentThumb = thumbs.get(position);


        holder.filterImageNameTextView.setText(currentThumb.filterName);
        GlideApp.with(context)
                .asBitmap()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .load(currentThumb.image)
                .into(holder.filterImageThumbnailImageView);
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
