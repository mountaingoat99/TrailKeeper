package RecyclerAdapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.singlecog.trailkeeper.R;

import java.util.List;

public class RecyclerViewNotifications extends RecyclerView.Adapter
        <RecyclerViewNotifications.ListItemViewHolder> {

    private List<String> items;
    private SparseBooleanArray selectedItems;

    public RecyclerViewNotifications(List<String> notifications) {
        items = notifications;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.subscriptions_recycle_layout, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        String trail = items.get(position);
        viewHolder.subscriptions.setText(trail);
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        //TODO Set Up Button Clicks

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
