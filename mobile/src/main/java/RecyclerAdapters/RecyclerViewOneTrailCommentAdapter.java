package RecyclerAdapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.singlecog.trailkeeper.R;

import java.util.List;

import models.ModelTrailComments;

public class RecyclerViewOneTrailCommentAdapter extends RecyclerView.Adapter
        <RecyclerViewOneTrailCommentAdapter.ListItemViewHolder> {

    private List<ModelTrailComments> items;
    private SparseBooleanArray selectedItems;

    public RecyclerViewOneTrailCommentAdapter(List<ModelTrailComments> modelData) {
        items = modelData;
        selectedItems = new SparseBooleanArray();
    }

//    public ModelTrailComments getItem(int position) {
//        return items.get(position);
//    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.one_trail_comment_recycler_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        ModelTrailComments model = items.get(position);
        viewHolder.trailComment.setText(model.TrailComments);
        if (model.CommentDate != null)
            viewHolder.commentDate.setText(model.CommentDate + " - ");
        else
            viewHolder.commentDate.setText("");
        viewHolder.commentUser.setText(model.CommentUserName);
        viewHolder.itemView.setActivated(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView trailComment;
        TextView commentDate;
        TextView commentUser;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            trailComment = (TextView) itemView.findViewById(R.id.txt_trail_comment);
            commentDate = (TextView)itemView.findViewById(R.id.txt_comment_date);
            commentUser = (TextView)itemView.findViewById(R.id.txt_comment_user);
        }
    }
}
