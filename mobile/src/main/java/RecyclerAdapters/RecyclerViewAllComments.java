package RecyclerAdapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.singlecog.trailkeeper.R;

import java.util.List;

import models.ModelTrailComments;

public class RecyclerViewAllComments extends RecyclerView.Adapter
            <RecyclerViewAllComments.ListItemViewHolder> {

    private List<ModelTrailComments> items;
    private static ModelTrailComments model;
    private Context context;

    public RecyclerViewAllComments(List<ModelTrailComments> modelData, Context context) {
        this.items = modelData;
        this.context = context;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.all_comments_recycler_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        model = items.get(position);
        viewHolder.trailName.setText(model.TrailName);
        viewHolder.commentDate.setText(model.CommentDate);
        viewHolder.userName.setText(model.CommentUserName);
        viewHolder.comment.setText(model.TrailComments);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public final static class ListItemViewHolder extends RecyclerView.ViewHolder{
        TextView trailName;
        TextView commentDate;
        TextView userName;
        TextView comment;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            trailName = (TextView) itemView.findViewById(R.id.txt_comment_trail_name);
            commentDate = (TextView) itemView.findViewById(R.id.txt_comment_date);
            userName = (TextView) itemView.findViewById(R.id.txt_comment_user);
            comment = (TextView) itemView.findViewById(R.id.txt_trail_comment);
        }
    }
}
