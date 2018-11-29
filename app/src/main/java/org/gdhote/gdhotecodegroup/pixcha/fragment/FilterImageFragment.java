package org.gdhote.gdhotecodegroup.pixcha.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailCallback;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.adapter.FilterThumbnailListAdapter;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.ImageBitmapViewModel;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private TextInputEditText captionEditText;
    private ImageBitmapViewModel mImageBitmapViewModel;
    private final Bitmap[] croppedImageBitmap = new Bitmap[1];
    private Bitmap mBitmap;


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
        captionEditText = view.findViewById(R.id.filter_image_caption_edit_text);
        mImageBitmapViewModel = ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class);
        mImageBitmapViewModel.getCroppedBitmap().observe(getActivity(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                croppedImageBitmap[0] = bitmap;
                mSquareImage.setImageBitmap(bitmap);
            }
        });

        mBitmap = croppedImageBitmap[0];

        mRecyclerView = view.findViewById(R.id.filter_image_list_rv);
        bindDataToAdapter();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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


                ThumbnailItem originalItem = new ThumbnailItem();
                originalItem.filterName = "Original";
                originalItem.image = thumbImage;
                originalItem.filter = new Filter();
                ThumbnailsManager.addThumb(originalItem);

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

    private Bitmap sBitmap = null;

    @Override
    public void onThumbnailClick(Filter filter) {
        if (filter != null) {
                Bitmap filterBitmap = filter.processFilter(Bitmap.createBitmap(mBitmap));
            mImageBitmapViewModel.setFilteredBitmap(filterBitmap);
            mSquareImage.setImageBitmap(filterBitmap);
            sBitmap = filterBitmap;
        }

        if (filter == null) {
            Bitmap filterBitmap = Bitmap.createBitmap(mBitmap);
            mImageBitmapViewModel.setFilteredBitmap(filterBitmap);
            mSquareImage.setImageBitmap(filterBitmap);
            sBitmap = filterBitmap;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_image_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.post_filter_image_menu:

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore firebaseDb = FirebaseFirestore.getInstance();
                final CollectionReference uploadColRef = firebaseDb.collection("uploads");
                final DocumentReference uploadDocRef = uploadColRef.document();
                final CollectionReference userUploadColRef = firebaseDb.collection("users")
                        .document(user.getUid()).collection("uploads");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference rootStorageReference = storage.getReference();
                StorageReference profileImagesStorageRef = rootStorageReference.child("uploaded_images");
                String fileName = "image" + "_" + uploadDocRef.getId() + ".jpg";
                final StorageReference fileRef = profileImagesStorageRef.child(fileName);

                Bitmap bitmap = (sBitmap != null ? sBitmap : this.mBitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] data = baos.toByteArray();

                openProgressDialog();

                final UploadTask uploadTask = fileRef.putBytes(data);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return fileRef.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        String userId = CurrentUser.getInstance().getId();
                        String captionText = captionEditText.getText().toString();
                        final Timestamp timestamp = Timestamp.now();

                        FeedPost post = new FeedPost(uploadDocRef.getId(), userId, imageUrl, captionText, timestamp, 0);

                        uploadDocRef.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Successfully add new post", Toast.LENGTH_SHORT).show();
                                Map<String, Object> entry = new HashMap<>();
                                entry.put("id", uploadDocRef.getId());
                                entry.put("uploadedAt", timestamp);

                                userUploadColRef.document(uploadDocRef.getId()).set(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        closeProgressDialog();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to add new post", Toast.LENGTH_SHORT).show();
                                fileRef.delete();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Unknown error occurred", Toast.LENGTH_SHORT).show();
                    }
                });


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    Dialog dialog;

    public void openProgressDialog() {
        View imageDialog = getLayoutInflater().inflate(R.layout.custom_image_dialog, null);
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(imageDialog);
        ImageView gifImage = imageDialog.findViewById(R.id.custom_image_dialog_image);
        DrawableImageViewTarget target = new DrawableImageViewTarget(gifImage);

        GlideApp.with(this)
                .load(R.drawable.ic_spinner)
                .placeholder(R.drawable.ic_spinner)
                .centerCrop()
                .into(target);
        dialog.show();
    }

    public void closeProgressDialog() {
        dialog.dismiss();
    }

}
