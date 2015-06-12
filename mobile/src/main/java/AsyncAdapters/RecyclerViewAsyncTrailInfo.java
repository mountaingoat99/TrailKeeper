package AsyncAdapters;

import android.app.Application;
import android.util.SparseArray;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import models.ModelTrails;

public class RecyclerViewAsyncTrailInfo extends Application {

    private static List<ModelTrails> trailOpenData;
    private static SparseArray<ModelTrails> trailOpenMap;

    @Override
    public void onCreate() {
        //Firebase.setAndroidContext(this);
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

        //Parse
        trailOpenData = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("trails");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    for (ParseObject parseObject : list){
                        ModelTrails model = new ModelTrails();
                        model.TrailName = parseObject.get("TrailName").toString();
                        model.TrailStatus = Integer.valueOf(parseObject.get("Status").toString());
                        model.TrailState = parseObject.get("State").toString();

                        trailOpenData.add(model);
                    }
                } else {
                    // lets do something else
                }
            }
        });


//        trailOpenMap = new SparseArray<>();
//        for (int i = 1; i < 21; i++) {
//            ModelTrails model = new ModelTrails();
//            model.TrailName = "Test Label No. " + i;
//            model.TrailStatus = 2;
//            model.TrailState = "WI";
//            trailOpenData.add(model);
//            trailOpenMap.put(model.TrailId, model);
//        }
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