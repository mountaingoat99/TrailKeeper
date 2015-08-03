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

import Helpers.StateListHelper;
import models.ModelTrails;


// this one gets the states that have trails in them
public class RecyclerViewFindTrailByState extends RecyclerView.Adapter
        <RecyclerViewFindTrailByState.ListItemViewHolder> {

    private final String LOG = "RecyclerFindTrailState";
    private List<String> items;
    private static String model;
    private int lastPosition = -1;
    private SparseBooleanArray selectedItems;
    private Context context;

    public RecyclerViewFindTrailByState(List<String> modelData, Context context) {
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
                inflate(R.layout.find_trail_bystate_recycle_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        model = items.get(position);
        viewHolder.states.setText(StateListHelper.GetStateName(model));
        // when a user clicks on a state show the Recycler view

        // or instantiate the other recycler view here
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView states;
        //RecyclerView trailsInState;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            states = (TextView) itemView.findViewById(R.id.state_list);
            //trailsInState = (RecyclerView)itemView.findViewById(R.id.find_trail_in_state_recycler_view);
        }
    }
}
