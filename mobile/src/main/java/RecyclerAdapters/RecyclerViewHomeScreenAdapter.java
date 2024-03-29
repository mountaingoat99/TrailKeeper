package RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.singlecog.trailkeeper.Activites.MapActivity;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import java.util.List;

import models.ModelTrails;

public class RecyclerViewHomeScreenAdapter extends RecyclerView.Adapter
        <RecyclerViewHomeScreenAdapter.HomeScreenListViewHolder> {

    private final String TAG = "RecyclerViewHomeAdapter";
    private List<ModelTrails> items;
    public static ModelTrails model;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    private SparseBooleanArray selectedItems;
    private Context context;
    // Global unit boolean added by Anatoliy
    private boolean globalUnitDefault;

    public RecyclerViewHomeScreenAdapter(List<ModelTrails> modelData, Context context, boolean globalUnitDefault) {
        items = modelData;
        selectedItems = new SparseBooleanArray();
        this.context = context;
        // Global unit added to constructor by Anatoliy
        this.globalUnitDefault = globalUnitDefault;

    }

    //public ModelTrails getItem(int position) {
        //return items.get(position);
    //}

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

//    public ParseGeoPoint getLocation(RecyclerView.ViewHolder item){
//        int id = item.getAdapterPosition();
//        ModelTrails m = items.get(id);
//        return m.GeoLocation;
//    }

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
        viewHolder.trailName.setText(model.getTrailName());
        viewHolder.trailCity.setText(model.getTrailCity());
        viewHolder.trailState.setText(model.getTrailState());
        viewHolder.trailDistance.setText(String.valueOf(model.distanceAway));
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        if (model.getTrailStatus() == 1) {
            viewHolder.trailStatus.setImageResource(R.drawable.status_closed);
        } else if (model.getTrailStatus() == 2) {
            viewHolder.trailStatus.setImageResource(R.drawable.status_open);
        } else {
            viewHolder.trailStatus.setImageResource(R.drawable.status_caution);
        }
        //global Unit check and change text by Anatoliy
        if (!globalUnitDefault){viewHolder.globalUnit.setText(" Km");}

        setAnimation(viewHolder, position);

        viewHolder.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String objectId = GetObjectID(viewHolder);
                Intent intent = new Intent(context, TrailScreen.class);
                intent.putExtra("objectID", objectId);
                intent.putExtra("fromNotification", false);
                Log.i(TAG, "Sending Intent From Home Screen to Trail Screen");
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelTrails model = getTrailModel(viewHolder);
                ParseGeoPoint point = model.GeoLocation;
                LatLng geoPoint = new LatLng(point.getLatitude(), point.getLongitude());
                Intent intent = new Intent(context, MapActivity.class);
                Bundle args = new Bundle();
                args.putParcelable("geoPoint", geoPoint);
                args.putString("trailName", model.getTrailName());
                args.putInt("trailStatus", model.getTrailStatus());
                args.putString("objectID", model.getObjectID());
                intent.putExtra("bundle", args);
                v.getContext().startActivity(intent);
            }
        });
    }

    // animation
    private void setAnimation(HomeScreenListViewHolder viewToAnimate, int position){
        // if the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(1000);
            //Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            View v = viewToAnimate.itemView;
            v.startAnimation(fadeIn);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class HomeScreenListViewHolder extends RecyclerView.ViewHolder {
        TextView trailName;
        TextView trailCity;
        TextView trailState;
        ImageView trailStatus;
        TextView trailDistance;
        Button btnHome;
        Button btnMap;
        //global unit text view by Anatoliy
        TextView globalUnit;

        public HomeScreenListViewHolder(View itemView) {
            super(itemView);
            trailName = (TextView) itemView.findViewById(R.id.txt_label_trail_name);
            trailCity = (TextView) itemView.findViewById(R.id.txt_label_trail_city);
            trailState = (TextView) itemView.findViewById(R.id.txt_label_trail_state);
            trailStatus = (ImageView)itemView.findViewById(R.id.txt_label_trail_status);
            trailDistance = (TextView)itemView.findViewById(R.id.txt_label_trail_distance);
            btnHome = (Button)itemView.findViewById(R.id.btn_trail_home);
            btnMap = (Button)itemView.findViewById(R.id.btn_trail_map);
            //global Unit by Anatoliy
            globalUnit = (TextView)itemView.findViewById(R.id.txt_label_miles);
        }
    }
}
