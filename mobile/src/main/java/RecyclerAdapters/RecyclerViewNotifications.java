package RecyclerAdapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.singlecog.trailkeeper.Activites.Notifications;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import java.util.List;

import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.ProgressDialogHelper;
import models.ModelTrails;

public class RecyclerViewNotifications extends RecyclerView.Adapter
        <RecyclerViewNotifications.ListItemViewHolder> {

    private final String LOG = "RecyclerVNotification";
    private List<String> items;
    private SparseBooleanArray selectedItems;
    private ConnectionDetector connectionDetector;
    private Context context;
    private ProgressDialog dialog;
    private String trailNameString;
    private ModelTrails modelTrails;
    private int itemToRemove;
    private View v;
    private Notifications notificationActivity;

    public RecyclerViewNotifications(List<String> notifications, Context context, View v, Notifications notificationActivity) {
        items = notifications;
        selectedItems = new SparseBooleanArray();
        this.notificationActivity = notificationActivity;
        this.context = context;
        this.v = v;
        connectionDetector = new ConnectionDetector(context);
        modelTrails = new ModelTrails(context, this);
    }

    public RecyclerViewNotifications(){}

    private String GetTrailName(RecyclerView.ViewHolder item) {
        itemToRemove = item.getAdapterPosition();
        trailNameString = items.get(item.getAdapterPosition());
        return items.get(item.getAdapterPosition());
    }

    public void SendToTrailScreen(ModelTrails trails, Context context) {
        Intent intent = new Intent(context, TrailScreen.class);
        intent.putExtra("trailID", trails.getTrailID());
        intent.putExtra("objectID", trails.getObjectID());
        context.startActivity(intent);
    }

    public void UpdateSubscriptionWasSuccessful(boolean valid, String message) {
        dialog.dismiss();
        if (valid) {
            removeItem(itemToRemove);
            Snackbar.make(v, "You have been un-subscribed from " + trailNameString, Snackbar.LENGTH_LONG).show();
            if (getItemCount() == 0) {
                notificationActivity.finish();
            }
            Log.i(LOG, "Subscription for " + trailNameString + " was updated");
        } else {
            Snackbar.make(v, "Something went wrong: " + message, Snackbar.LENGTH_LONG).show();
            Log.i(LOG, "Subscription for " + trailNameString + " was not updated");
        }
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        items.remove(position);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.subscriptions_recycle_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        String trail = items.get(position);
        viewHolder.subscriptions.setText(trail);
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        viewHolder.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelTrails trails = new ModelTrails();
                trails.GetTrailIDs(GetTrailName(viewHolder), context);
            }
        });

        viewHolder.btnUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    dialog = ProgressDialogHelper.ShowProgressDialog(context, "Updating Subscription");
                    modelTrails.SubscribeToChannel(GetTrailName(viewHolder), 1, LOG);
                } else {
                    AlertDialogHelper.showAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView subscriptions;
        Button btnHome;
        Button btnUnsubscribe;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            subscriptions = (TextView) itemView.findViewById(R.id.subscription_trails);
            btnHome = (Button) itemView.findViewById(R.id.btn_trail_home);
            btnUnsubscribe = (Button) itemView.findViewById(R.id.btn_unsubscribe);
        }
    }
}
