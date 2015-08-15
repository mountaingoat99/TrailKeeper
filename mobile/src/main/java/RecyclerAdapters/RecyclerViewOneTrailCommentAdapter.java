package RecyclerAdapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import models.ModelTrailComments;

public class RecyclerViewOneTrailCommentAdapter extends RecyclerView.Adapter
        <RecyclerViewOneTrailCommentAdapter.ListItemViewHolder> {

    private List<ModelTrailComments> items;
    private SparseBooleanArray selectedItems;

    public RecyclerViewOneTrailCommentAdapter(List<ModelTrailComments> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        selectedItems = new SparseBooleanArray();
    }

    /**
     * Adds and item into the underlying data set
     * at the position passed into the method.
     *
     * @param newModelData The item to add to the data set.
     * @param position The index of the item to add.
     */
    public void addData(ModelTrailComments newModelData, int position) {
        items.add(position, newModelData);
        notifyItemInserted(position);
    }

    /**
     * Removes the item that currently is at the passed in position from the
     * underlying data set.
     *
     * @param position The index of the item to remove.
     */
    public void removeData(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public ModelTrailComments getItem(int position) {
        return items.get(position);
    }

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

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
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
