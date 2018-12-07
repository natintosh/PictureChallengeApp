package org.gdhote.gdhotecodegroup.pixcha.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.model.Comment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentListAdapterViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentListAdapter(Context context) {
        this.context = context;
        commentList = new ArrayList<>();
    }

    public void setDataSet(List<Comment> dataSet) {
        commentList = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);

        return new CommentListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapterViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.displayNameText.setText(comment.getDisplayName());
        holder.commentMsg.setText(comment.getMessage());
        holder.uploadedAt.setText(DateUtils.getRelativeTimeSpanString(comment.getUploadedAt().toDate().getTime()));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentListAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView displayNameText;
        private TextView commentMsg;
        private TextView uploadedAt;

        CommentListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            displayNameText = itemView.findViewById(R.id.comment_display_name);
            commentMsg = itemView.findViewById(R.id.comment_message);
            uploadedAt = itemView.findViewById(R.id.comment_time_text);
        }
    }
}
