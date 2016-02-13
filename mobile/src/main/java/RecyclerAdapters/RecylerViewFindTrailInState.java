package RecyclerAdapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.singlecog.trailkeeper.Activites.MapActivity;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.Activites.TrailMap;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.BuildConfig;
import com.singlecog.trailkeeper.R;

import java.util.List;

import Helpers.AlertDialogHelper;
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
        final ModelTrails model = items.get(position);
        viewHolder.trails.setText(model.getTrailName());
        final String trailName = model.getTrailName();

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
                final String objectId = GetObjectID(viewHolder);
                // lets add a dialog here instead letting the user either got to the trail screen
                // map, or subscribe to the trail
                final Dialog chooseDialog = new Dialog(context);
                chooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                chooseDialog.setContentView(R.layout.dialog_findtrail_options);
                Button btnTrailHome = (Button)chooseDialog.findViewById(R.id.btn_trailHome);
                Button btnTrailMap = (Button)chooseDialog.findViewById(R.id.btn_TrailMap);
                Button btnSubscribe = (Button)chooseDialog.findViewById(R.id.btn_subscribeToTrail);
                Button btnCancel = (Button)chooseDialog.findViewById(R.id.btn_cancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseDialog.dismiss();
                    }
                });

                btnTrailHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TrailScreen.class);
                        intent.putExtra("objectID", objectId);
                        v.getContext().startActivity(intent);
                        chooseDialog.dismiss();
                    }
                });

                btnTrailMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MapActivity.class);
                        Bundle args = new Bundle();
                        ParseGeoPoint point = model.GeoLocation;
                        final LatLng geoPoint = new LatLng(point.getLatitude(), point.getLongitude());
                        args.putParcelable("geoPoint", geoPoint);
                        args.putString("trailName", model.getTrailName());
                        args.putInt("trailStatus", model.getTrailStatus());
                        args.putString("objectID", objectId);
                        intent.putExtra("bundle", args);
                        v.getContext().startActivity(intent);
                        chooseDialog.dismiss();
                    }
                });

                btnSubscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseDialog.dismiss();
                        ModelTrails modelTrails = new ModelTrails();
                        modelTrails.SubscribeToChannel(trailName, 0);
                        AlertDialogHelper.showCustomAlertDialog(context, trailName, "You will now receive notifications for " + trailName);
                    }
                });

                chooseDialog.show();

                //Intent intent = new Intent(context, TrailScreen.class);
                //intent.putExtra("objectID", objectId);
                //v.getContext().startActivity(intent);
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
