package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.recyclerview.widget.RecyclerView;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileListAdapterViewHolder> {
    private List<FeedPost> feedPostList;
    private Context context;

    public ProfileListAdapter(Context context) {
        feedPostList = new ArrayList<>();
        this.context = context;
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
        final FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        final FeedPost post = feedPostList.get(position);
        final CollectionReference likesColRef = firestoreDb.collection("uploads")
                .document(post.getId()).collection("likes");

        DocumentReference userDocRef = firestoreDb.collection("users").document(post.getUploadedBy());

        final User[] user = new User[1];
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = documentSnapshot.toObject(User.class);

                likesColRef.whereEqualTo("id", user[0].getId()).limit(1)
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
                        .into(holder.profileImage);

                holder.displayName.setText(user[0].getDisplayName());

                GlideApp.with(context)
                        .asBitmap()
                        .placeholder(new ColorDrawable(Color.WHITE))
                        .load(post.getImageUrl())
                        .into(holder.feedImage);

                holder.feedCaptionText.setText(post.getCaptionText());


                likesColRef.addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;

                        String likes = Integer.toString(queryDocumentSnapshots != null ? queryDocumentSnapshots.size() : 0);
                        holder.likesText.setText(likes);
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
                    DocumentReference likeDocRef = likesColRef.document(user[0].getId());
                    Map<String, String> likeEntry = new HashMap<>();
                    likeEntry.put("id", user[0].getId());
                    likeDocRef.set(likeEntry).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "is Checked and like added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (!isChecked) {
                    DocumentReference likeDocRef = likesColRef.document(user[0].getId());
                    Map<String, String> likeEntry = new HashMap<>();
                    likeEntry.put("id", user[0].getId());
                    likeDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "not checked and like removed", Toast.LENGTH_SHORT).show();
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


    class ProfileListAdapterViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView profileImage;
        private TextView displayName;
        private SquareImageView feedImage;
        private CheckBox likeCheckBox;
        private TextView feedCaptionText;
        private TextView likesText;
        private TextView timeText;

        ProfileListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.feed_circular_profile_image);
            displayName = itemView.findViewById(R.id.feed_user_name_text);
            feedImage = itemView.findViewById(R.id.feed_square_content_image);
            likeCheckBox = itemView.findViewById(R.id.feed_like_check_box);
            feedCaptionText = itemView.findViewById(R.id.feed_caption_text);
            likesText = itemView.findViewById(R.id.feed_content_likes_text);
            timeText = itemView.findViewById(R.id.feed_uploaded_time_tv);

        }
    }
}
