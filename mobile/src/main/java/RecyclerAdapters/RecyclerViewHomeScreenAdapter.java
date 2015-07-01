package RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.singlecog.trailkeeper.Activites.Map;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.TrailStatusHelper;
import models.ModelTrails;

public class RecyclerViewHomeScreenAdapter extends RecyclerView.Adapter
        <RecyclerViewHomeScreenAdapter.HomeScreenListViewHolder> {

    private List<ModelTrails> items;
    public static ModelTrails model;
    private SparseBooleanArray selectedItems;
    private Context context;

    public RecyclerViewHomeScreenAdapter(List<ModelTrails> modelData, Context context) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        selectedItems = new SparseBooleanArray();
        this.context = context;
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

    public ParseGeoPoint getLocation(RecyclerView.ViewHolder item){
        int id = item.getAdapterPosition();
        ModelTrails m = items.get(id);
        return m.GeoLocation;
    }

    public ModelTrails getTrailModel(RecyclerView.ViewHolder item) {
        int id = item.getAdapterPosition();
        return items.get(id);
    }

    @Override
    public HomeScreenListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.home_screen_card_view, viewGroup, false);
        return new HomeScreenListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HomeScreenListViewHolder viewHolder, int position) {

        model = items.get(position);
        viewHolder.trailName.setText(model.TrailName);
        viewHolder.trailCity.setText(model.TrailCity);
        viewHolder.trailState.setText(model.TrailState);
        viewHolder.trailDistance.setText(String.valueOf(model.distance));
        //String statusName = TrailStatusHelper.ConvertTrailStatus(model);
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        if (model.TrailStatus == 1) {
            viewHolder.trailStatus.setImageResource(R.mipmap.red_closed);
        } else if (model.TrailStatus == 2) {
            viewHolder.trailStatus.setImageResource(R.mipmap.green_open);
        } else {
            viewHolder.trailStatus.setImageResource(R.mipmap.yellow_unknown);
        }

        viewHolder.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = getTrailID(viewHolder);
                Intent intent = new Intent(context, TrailScreen.class);
                intent.putExtra("trailID", id);
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelTrails model  = getTrailModel(viewHolder);
                ParseGeoPoint point = model.GeoLocation;
                LatLng geoPoint = new LatLng(point.getLatitude(), point.getLongitude());
                Intent intent = new Intent(context, Map.class);
                Bundle args = new Bundle();
                args.putParcelable("geoPoint", geoPoint);
                args.putString("trailName", model.TrailName);
                args.putInt("trailStatus", model.TrailStatus);
                intent.putExtra("bundle", args);
                v.getContext().startActivity(intent);
            }
        });
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

    public final static class HomeScreenListViewHolder extends RecyclerView.ViewHolder {
        TextView trailName;
        TextView trailCity;
        TextView trailState;
        ImageView trailStatus;
        TextView trailDistance;
        Button btnHome;
        Button btnMap;

        public HomeScreenListViewHolder(View itemView) {
            super(itemView);
            trailName = (TextView) itemView.findViewById(R.id.txt_label_trail_name);
            trailCity = (TextView) itemView.findViewById(R.id.txt_label_trail_city);
            trailState = (TextView) itemView.findViewById(R.id.txt_label_trail_state);
            trailStatus = (ImageView)itemView.findViewById(R.id.txt_label_trail_status);
            trailDistance = (TextView)itemView.findViewById(R.id.txt_label_trail_distance);
            btnHome = (Button)itemView.findViewById(R.id.btn_trail_home);
            btnMap = (Button)itemView.findViewById(R.id.btn_trail_map);
        }
    }
}
