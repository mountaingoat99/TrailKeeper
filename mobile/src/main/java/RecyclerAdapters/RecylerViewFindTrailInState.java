package RecyclerAdapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import java.util.List;

import Helpers.GeoLocationHelper;
import models.ModelTrails;

public class RecylerViewFindTrailInState extends RecyclerView.Adapter
        <RecylerViewFindTrailInState.ListItemViewHolder> {

    private List<ModelTrails> items;
    private SparseBooleanArray selectedItems;
    private Context context;
    // Global unit boolean added by Anatoliy
    private boolean globalUnitDefault;

    public RecylerViewFindTrailInState(List<ModelTrails> modelData, Context context, boolean globalUnitDefault) {
        this.items = modelData;
        selectedItems = new SparseBooleanArray();
        this.context = context;
        // Global unit added to constructor by Anatoliy
        this.globalUnitDefault = globalUnitDefault;
    }

//    public int getTrailID(RecyclerView.ViewHolder item){
//        // use the view holder to get the Adapter Position
//        int id =  item.getAdapterPosition();
//        // then use the AdapterPosition to get the Model
//        ModelTrails m = items.get(id);
//        // return the TrailID from the model
//        return m.TrailID;
//    }

    public String GetObjectID(RecyclerView.ViewHolder item) {
        int id = item.getAdapterPosition();
        ModelTrails m = items.get(id);
        return m.getObjectID();
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.find_trail_instate_recycler_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        ModelTrails model = items.get(position);
        viewHolder.trails.setText(model.getTrailName());
        viewHolder.cities.setText(model.getTrailCity());
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        if (model.getTrailStatus() == 1) {
            viewHolder.trailStatus.setImageResource(R.drawable.status_closed);
        } else if (model.getTrailStatus() == 2) {
            viewHolder.trailStatus.setImageResource(R.drawable.status_open);
        } else {
            viewHolder.trailStatus.setImageResource(R.drawable.status_caution);
        }
        //by Anatoliy
        if (globalUnitDefault) {
            int distanceAway = Math.round(GeoLocationHelper.GetClosestTrails(model, TrailKeeperApplication.home) * 100) / 100;
            viewHolder.distance.setText(String.valueOf(distanceAway + " Miles"));
        }
        else {
            int distanceAway = (int)Math.round(GeoLocationHelper.GetClosestTrails(model, TrailKeeperApplication.home)*1.609344 * 100) / 100;
            viewHolder.distance.setText(String.valueOf(distanceAway + " Km"));
        }

        viewHolder.trailTouchPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String objectId = GetObjectID(viewHolder);
                Intent intent = new Intent(context, TrailScreen.class);
                intent.putExtra("objectID", objectId);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView trails;
        TextView cities;
        ImageView trailStatus;
        TextView distance;
        LinearLayout trailTouchPoint;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            trails = (TextView) itemView.findViewById(R.id.trails_in_state_list);
            cities = (TextView) itemView.findViewById(R.id.trails_in_state_cities);
            trailStatus = (ImageView)itemView.findViewById(R.id.txt_label_trail_status);
            distance = (TextView)itemView.findViewById(R.id.trails_distance);
            trailTouchPoint = (LinearLayout)itemView.findViewById(R.id.container_inner_item);
        }
    }
}
