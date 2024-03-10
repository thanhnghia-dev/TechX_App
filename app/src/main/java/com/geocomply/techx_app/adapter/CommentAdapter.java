package com.geocomply.techx_app.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geocomply.techx_app.R;
import com.geocomply.techx_app.model.Comment;

import java.text.DecimalFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bindData(commentList.get(position));
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRating, tvContent, tvDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.fullName);
            tvRating = itemView.findViewById(R.id.amount);
            tvContent = itemView.findViewById(R.id.content);
            tvDate = itemView.findViewById(R.id.commentDate);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Comment comment) {
//            tvDate.setText(comment.getCreatedAt());
            tvName.setText(comment.getUserIdNavigation().getName());
            tvRating.setText(String.valueOf(comment.getRating()));
            tvContent.setText(comment.getContent());

        }
    }
}
