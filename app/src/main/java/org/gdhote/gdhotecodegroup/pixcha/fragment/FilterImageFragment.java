package org.gdhote.gdhotecodegroup.pixcha.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailCallback;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.adapter.FilterThumbnailListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.ImageBitmapViewModel;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterImageFragment extends Fragment implements ThumbnailCallback {


    public FilterImageFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    private SquareImageView mSquareImage;
    private ImageBitmapViewModel mImageBitmapViewModel;
    private final Bitmap[] croppedImageBitmap = new Bitmap[1];
    private Bitmap bitmap;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_image, container, false);

        getActivity().setTitle("Filter Image");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSquareImage = view.findViewById(R.id.filter_square_image_view);
        mImageBitmapViewModel = ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class);
        mImageBitmapViewModel.getCroppedBitmap().observe(getActivity(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                croppedImageBitmap[0] = bitmap;
                mSquareImage.setImageBitmap(bitmap);
            }
        });

        bitmap = croppedImageBitmap[0];

        mRecyclerView = view.findViewById(R.id.filter_image_list_rv);
        bindDataToAdapter();

        return view;
    }

    private void bindDataToAdapter() {
        final Context context = getActivity();
        Handler handler = new Handler();
        final FilterThumbnailListAdapter listAdapter = new FilterThumbnailListAdapter(this);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                @NonNull
                Bitmap thumbImage = croppedImageBitmap[0];
                ThumbnailsManager.clearThumbs();
                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for (Filter filter : filters) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.filterName = filter.getName();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }


                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                listAdapter.setThumbs(thumbs);
                mRecyclerView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        Bitmap filterBitmap = filter.processFilter(Bitmap.createBitmap(bitmap));
        mImageBitmapViewModel.setFilteredBitmap(filterBitmap);
        mSquareImage.setImageBitmap(filterBitmap);
    }
}
