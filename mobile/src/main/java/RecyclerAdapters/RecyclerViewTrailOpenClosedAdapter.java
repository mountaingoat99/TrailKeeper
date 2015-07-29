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

import models.ModelTrails;

public class RecyclerViewTrailOpenClosedAdapter extends RecyclerView.Adapter
        <RecyclerViewTrailOpenClosedAdapter.ListItemViewHolder> {

    private List<ModelTrails> items;
    public static ModelTrails model;
    private SparseBooleanArray selectedItems;

    public RecyclerViewTrailOpenClosedAdapter(List<ModelTrails> modelData) {
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
     * @param position The index of the item to remove.
     */
    public void addData(ModelTrails newModelData, int position) {
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

    public ModelTrails getItem(int position) {
        return items.get(position);
    }

    public int getTrailID(RecyclerView.ViewHolder item){
        // use the view holder to get the Adapter Position
        int id =  item.getAdapterPosition();
        // then use the AdapterPosition to get the Model
        ModelTrails m = items.get(id);
        // return the TrailID from the model
        return m.TrailID;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.trail_open_closed_list, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        model = items.get(position);
        viewHolder.trailID.setText(String.valueOf(model.TrailID));
        viewHolder.trailName.setText(model.TrailName);
        String statusName = ModelTrails.ConvertTrailStatus(model);
        viewHolder.trailStatus.setText(statusName);
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
        TextView trailName;
        TextView trailStatus;
        TextView trailID;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            trailName = (TextView) itemView.findViewById(R.id.txt_label_trail_name);
            trailStatus = (TextView)itemView.findViewById(R.id.txt_label_trail_status);
            trailID = (TextView)itemView.findViewById(R.id.txt_label_trailID);
        }
    }
}
