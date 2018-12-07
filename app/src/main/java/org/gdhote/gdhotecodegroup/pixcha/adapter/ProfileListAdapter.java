package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;
import org.gdhote.gdhotecodegroup.pixcha.model.User;
import org.gdhote.gdhotecodegroup.pixcha.ui.CircularImageView;
import org.gdhote.gdhotecodegroup.pixcha.ui.SquareImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileListAdapterViewHolder> {
    private List<FeedPost> feedPostList;
    private Context context;
    private SetFeedListItemCallback itemCallback;

    public interface SetFeedListItemCallback {

        void onSquareImageLongPress(String postId);

        void onPopUpMenuPress(User user, FeedPost feedPost, ImageButton imageButton);
    }

    private void OnSquareImageLongPress(String postId) {
        if (itemCallback != null) itemCallback.onSquareImageLongPress(postId);
    }

    private void OnPopUpMenuPress(User user, FeedPost feedPost, ImageButton imageButton) {
        if (itemCallback != null) itemCallback.onPopUpMenuPress(user, feedPost, imageButton);
    }

    public ProfileListAdapter(Context context, SetFeedListItemCallback callback) {
        feedPostList = new ArrayList<>();
        this.context = context;
        itemCallback = callback;
    }

    public void setDataSet(List<FeedPost> feedList) {
        feedPostList = feedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_list_item, parent, false);
        return new ProfileListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileListAdapterViewHolder holder, int position) {
        holder.contentLayout.setVisibility(View.GONE);

        final FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        final FeedPost post = feedPostList.get(position);
        final CollectionReference uploadscolRef = firestoreDb.collection("uploads");
        final DocumentReference feedDocRef = uploadscolRef.document(post.getId());
        final CollectionReference likesColRef = feedDocRef.collection("likes");

        DocumentReference userDocRef = firestoreDb.collection("users").document(post.getUploadedBy());

        final User[] user = new User[1];
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = documentSnapshot.toObject(User.class);

                likesColRef.whereEqualTo("id", CurrentUser.getInstance().getId()).limit(1)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isEmpty = task.getResult().isEmpty();
                            if (!isEmpty) {
                                holder.likeCheckBox.setChecked(true);
                            }
                        }
                    }
                });

                GlideApp.with(context)
                        .asBitmap()
                        .placeholder(new ColorDrawable(Color.LTGRAY))
                        .load(user[0].getProfileImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.profileImage);

                holder.displayName.setText(user[0].getDisplayName());

                GlideApp.with(context)
                        .asBitmap()
                        .placeholder(new ColorDrawable(Color.LTGRAY))
                        .load(post.getImageUrl())
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                                if (resource != null) {
                                    Palette p = Palette.from(resource).generate();
                                    Palette.Swatch darkMutedColor = p.getDarkMutedSwatch();
                                    holder.contentBg.setBackground(new ColorDrawable(darkMutedColor.getRgb()));
                                    holder.feedCaptionText.setTextColor(darkMutedColor.getBodyTextColor());
                                }
                                return false;
                            }
                        })
                        .into(holder.feedImage);

                String captionText = ((post.getCaptionText().isEmpty() || post.getCaptionText() == null) ? "No caption" : post.getCaptionText());
                holder.feedCaptionText.setText(captionText);


                likesColRef.addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;

                        int likesNo = queryDocumentSnapshots.size();
                        String likes = (likesNo <= 1 ? likesNo + " like" : likesNo + " likes");
                        holder.likesNoText.setText(likes);
                    }
                });

                holder.timeText.setText(DateUtils.getRelativeTimeSpanString(post.getUploadedAt().toDate().getTime()));
            }
        });

        holder.likeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();

                if (isChecked) {
                    DocumentReference likeDocRef = likesColRef.document(CurrentUser.getInstance().getId());
                    Map<String, String> likeEntry = new HashMap<>();
                    likeEntry.put("id", CurrentUser.getInstance().getId());
                    likeDocRef.set(likeEntry);
                }

                if (!isChecked) {
                    DocumentReference likeDocRef = likesColRef.document(CurrentUser.getInstance().getId());
                    Map<String, String> likeEntry = new HashMap<>();
                    likeEntry.put("id", CurrentUser.getInstance().getId());
                    likeDocRef.delete();
                }
            }
        });

        holder.feedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.contentLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.contentLayout.setVisibility(View.GONE);
            }
        });

        holder.contentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                OnSquareImageLongPress(post.getId());
                return false;
            }
        });

        holder.feedImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                OnSquareImageLongPress(post.getId());
                return false;
            }
        });

        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnPopUpMenuPress(user[0], post, holder.popupButton);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (feedPostList != null ? feedPostList.size() : 0);
    }


    class ProfileListAdapterViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView profileImage;
        private TextView displayName;
        private SquareImageView feedImage;
        private ConstraintLayout contentLayout;
        private CheckBox likeCheckBox;
        private TextView feedCaptionText;
        private TextView likesNoText;
        private TextView timeText;
        private View contentBg;
        private ImageButton popupButton;


        ProfileListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.feed_circular_profile_image);
            displayName = itemView.findViewById(R.id.feed_user_name_text);
            feedImage = itemView.findViewById(R.id.feed_square_content_image);
            contentLayout = itemView.findViewById(R.id.feed_content_layout);
            likeCheckBox = itemView.findViewById(R.id.feed_like_check_box);
            feedCaptionText = itemView.findViewById(R.id.feed_caption_text);
            likesNoText = itemView.findViewById(R.id.feed_content_likes_text);
            timeText = itemView.findViewById(R.id.feed_uploaded_time_tv);
            contentBg = itemView.findViewById(R.id.feed_content_background);
            popupButton = itemView.findViewById(R.id.feed_popup_menu_btn);
        }
    }
}
