package AsyncAdapters;

import android.app.Application;
import android.app.DownloadManager;
import android.util.SparseArray;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import models.ModelTrails;

public class RecyclerViewAsyncTrailInfo extends Application {

    private static List<ModelTrails> trailOpenData;
    private static SparseArray<ModelTrails> trailOpenMap;

    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
        super.onCreate();

        // Firebase
//        Firebase ref = new Firebase("https://luminous-heat-2687.firebaseio.com/trailKeeper/trails");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                trailOpenData = new ArrayList<>();
//                trailOpenMap = new SparseArray<>();
//                ModelTrails model = new ModelTrails();
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    for (DataSnapshot nextChild : child.getChildren()){
//                        model.TrailName = nextChild.child("trailName").toString();
//                        model.TrailStatus = Integer.getInteger(nextChild.child("status").toString());
//                        model.TrailState = nextChild.child("state").toString();
//
//                        trailOpenData.add(model);
//                        trailOpenMap.put(model.TrailId, model);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


        trailOpenData = new ArrayList<>();
        trailOpenMap = new SparseArray<>();
        for (int i = 1; i < 21; i++) {
            ModelTrails model = new ModelTrails();
            model.TrailName = "Test Label No. " + i;
            model.TrailStatus = 2;
            model.TrailState = "WI";
            trailOpenData.add(model);
            trailOpenMap.put(model.TrailId, model);
        }
    }

    public static List<ModelTrails> getTrailOpenData() {
        return new ArrayList<>(trailOpenData);
    }

    public static List<ModelTrails> addItemToList(ModelTrails model, int position) {
        trailOpenData.add(position, model);
        trailOpenMap.put(model.TrailId, model);
        return new ArrayList<>(trailOpenData);
    }

    public static List<ModelTrails> removeItemFromList(int position) {
        trailOpenData.remove(position);
        trailOpenMap.remove(trailOpenData.get(position).TrailId);
        return new ArrayList<>(trailOpenData);
    }

    public static ModelTrails findById(int id) {
        return trailOpenMap.get(id);
    }
}