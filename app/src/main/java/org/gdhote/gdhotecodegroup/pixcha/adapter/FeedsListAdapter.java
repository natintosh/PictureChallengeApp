package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsListAdapter extends RecyclerView.Adapter<FeedsListAdapter.FeedsListAdapterViewHolder> {

    private Context context;
    private List<FeedPost> feedPostList;

    public FeedsListAdapter(Context context) {
        feedPostList = new ArrayList<>();
        this.context = context;
    }

    public void setDataSet(List<FeedPost> dataSet) {
        feedPostList = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedsListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_list_item, parent, false);
        return new FeedsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedsListAdapterViewHolder holder, final int position) {
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
                Toast.makeText(context, "Long Click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.popupButton);
                popupMenu.getMenuInflater().inflate(R.menu.feeds_popup_menu, popupMenu.getMenu());
                if (user[0] != null) {
                    if (!user[0].getId().equals(CurrentUser.getInstance().getId())) {
                        MenuItem item = popupMenu.getMenu().findItem(R.id.feed_popup_more_menu);
                        if (item != null) {
                            item.setVisible(false);
                        }
                    }
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.feed_popup_report_action_menu:
                                    final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
                                    View dialogView = dialogBuilder.getLayoutInflater().inflate(R.layout.dialog_custom_report, null);

                                    final EditText reportEditText = dialogView.findViewById(R.id.report_dialog_edit_text);
                                    Button cancelButton = dialogView.findViewById(R.id.report_dialog_cancel_btn);
                                    Button submitButton = dialogView.findViewById(R.id.report_dialog_submit_btn);

                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                        }
                                    });

                                    submitButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Map<String, Object> reportMap = new HashMap<>();
                                            reportMap.put("reportedBy", CurrentUser.getInstance().getId());
                                            reportMap.put("reportedPost", post.getId());
                                            reportMap.put("reportedText", reportEditText.getText().toString());

                                            firestoreDb.collection("reports").document()
                                                    .set(reportMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Reported", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            dialogBuilder.dismiss();
                                        }
                                    });

                                    dialogBuilder.setView(dialogView);
                                    dialogBuilder.show();
                                    break;
                                case R.id.feed_popup_delete_action_menu:
                                    final WriteBatch batch = firestoreDb.batch();

                                    StorageReference rootStorageReference = FirebaseStorage.getInstance().getReference();
                                    StorageReference profileImagesStorageRef = rootStorageReference.child("uploaded_images");
                                    String fileName = "image" + "_" + post.getId() + ".jpg";
                                    final StorageReference fileRef = profileImagesStorageRef.child(fileName);

                                    fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            feedDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    likesColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if (task.isSuccessful() || !task.getResult().isEmpty()) {
                                                                for (DocumentSnapshot snapshot : task.getResult()) {
                                                                    batch.delete(snapshot.getReference());
                                                                }
                                                            }
                                                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (feedPostList != null ? feedPostList.size() : 0);
    }

    class FeedsListAdapterViewHolder extends RecyclerView.ViewHolder {

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


        FeedsListAdapterViewHolder(@NonNull View itemView) {
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
