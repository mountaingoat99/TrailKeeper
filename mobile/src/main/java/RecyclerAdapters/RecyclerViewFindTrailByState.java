package RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.MyLinearLayoutManager;
import Helpers.StateListHelper;
import models.ModelTrails;

// this one gets the states that have trails in them
public class RecyclerViewFindTrailByState extends RecyclerView.Adapter
        <RecyclerViewFindTrailByState.ListItemViewHolder> {

    private final String LOG = "RecyclerFindTrailState";
    private List<String> items;
    private static String model;
    private static List<ModelTrails> trailsByState;
    private SparseBooleanArray selectedItems;
    private Context context;

    private RecylerViewFindTrailInState mFindTrailInStateAdapter;


    public RecyclerViewFindTrailByState(List<String> modelData, Context context) {
        if (modelData == null) {
            throw new IllegalArgumentException("ModelData Must not be null");
        }
        this.items = modelData;
        selectedItems = new SparseBooleanArray();
        this.context = context;
    }

    public String GetState(RecyclerView.ViewHolder item) {
        int id = item.getAdapterPosition();
        return items.get(id);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.find_trail_bystate_recycle_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        model = items.get(position);
        viewHolder.states.setText(StateListHelper.GetStateName(model));
        viewHolder.expand.setImageResource(R.mipmap.ic_action_expand);
        viewHolder.showTrails = false;  // decides if we show or un-show the trails
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        viewHolder.stateTouchPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.showTrails) {
                    viewHolder.trailsInState.setVisibility(View.VISIBLE);
                    viewHolder.expand.setImageResource(R.mipmap.ic_action_collapse);
                    trailsByState = new ArrayList<>();
                    String state = GetState(viewHolder);
                    MyLinearLayoutManager trailsViewLinearLayout = new MyLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    viewHolder.trailsInState.setLayoutManager(trailsViewLinearLayout);
                    viewHolder.trailsInState.setHasFixedSize(true);

                    // get the list of states from the local datastore
                    // and add them to a new List of ModelTrails
                    List<ParseObject> trails = ModelTrails.GetTrailsByState(state);
                    for (ParseObject parseObject : trails) {
                        ModelTrails trail = new ModelTrails();
                        trail.TrailID = parseObject.getInt("TrailID");
                        trail.setObjectID(parseObject.getObjectId());
                        trail.TrailName = parseObject.get("TrailName").toString();
                        trail.TrailCity = parseObject.get("City").toString();
                        trail.TrailStatus = parseObject.getInt("Status");
                        trail.GeoLocation = parseObject.getParseGeoPoint("GeoLocation");

                        trailsByState.add(trail);
                    }
                    // set the show trails to true
                    viewHolder.showTrails = true;
                    // send the trail list to the next adapter
                    mFindTrailInStateAdapter = new RecylerViewFindTrailInState(trailsByState, context);
                    viewHolder.trailsInState.setAdapter(mFindTrailInStateAdapter);
                    viewHolder.trailsInState.setItemAnimator(new DefaultItemAnimator());
                } else {
                    viewHolder.expand.setImageResource(R.mipmap.ic_action_expand);
                    viewHolder.trailsInState.setVisibility(View.GONE);
                    viewHolder.showTrails = false;
                }

                // when a user clicks on a state show the Recycler view

                // or instantiate the other recycler view here
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder{
        TextView states;
        RecyclerView trailsInState;
        ImageView expand;
        LinearLayout stateTouchPoint;
        boolean showTrails;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            states = (TextView) itemView.findViewById(R.id.state_list);
            trailsInState = (RecyclerView)itemView.findViewById(R.id.find_trail_in_state_recycler_view);
            expand = (ImageView)itemView.findViewById(R.id.image_expand);
            stateTouchPoint = (LinearLayout)itemView.findViewById(R.id.container_inner_item);
            showTrails = false;
        }
    }
}
