package RecyclerAdapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.singlecog.trailkeeper.R;

import java.util.List;

import models.ModelTrails;

public class RecylerViewFindTrailInState extends RecyclerView.Adapter
        <RecylerViewFindTrailInState.ListItemViewHolder> {

    private final String LOG = "RecyclerFindTrailInState";
    private List<ModelTrails> items;
    private static ModelTrails model;
    private int lastPosition = -1;
    private SparseBooleanArray selectedItems;
    private Context context;

    public RecylerViewFindTrailInState(List<ModelTrails> modelData, Context context) {
        if (modelData == null) {
            throw new IllegalArgumentException("ModelData Must not be null");
        }
        this.items = modelData;
        selectedItems = new SparseBooleanArray();
        this.context = context;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.find_trail_instate_recycler_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        model = items.get(position);
        viewHolder.trails.setText(model.TrailName);
        viewHolder.cities.setText(model.TrailCity);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView trails;
        TextView cities;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            trails = (TextView) itemView.findViewById(R.id.trails_in_state_list);
            cities = (TextView) itemView.findViewById(R.id.trails_in_state_cities);
        }
    }
}
