package AsyncAdapters;

import android.app.Application;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.ModelOpenClosedTrails;

public class RecyclerViewAsyncTrailInfo extends Application {

    private static List<ModelOpenClosedTrails> trailOpenData;
    private static SparseArray<ModelOpenClosedTrails> trailOpenMap;

    @Override
    public void onCreate() {
        super.onCreate();
        trailOpenData = new ArrayList<>();
        trailOpenMap = new SparseArray<>();
        for (int i = 1; i < 21; i++) {
            ModelOpenClosedTrails model = new ModelOpenClosedTrails();
            model.TrailName = "Test Label No. " + i;
            model.TrailStatus = 2;
            trailOpenData.add(model);
            trailOpenMap.put(model.TrailId, model);
        }
    }

    public static List<ModelOpenClosedTrails> getTrailOpenData() {
        return new ArrayList<>(trailOpenData);
    }

    public static List<ModelOpenClosedTrails> addItemToList(ModelOpenClosedTrails model, int position) {
        trailOpenData.add(position, model);
        trailOpenMap.put(model.TrailId, model);
        return new ArrayList<>(trailOpenData);
    }

    public static List<ModelOpenClosedTrails> removeItemFromList(int position) {
        trailOpenData.remove(position);
        trailOpenMap.remove(trailOpenData.get(position).TrailId);
        return new ArrayList<>(trailOpenData);
    }

    public static ModelOpenClosedTrails findById(int id) {
        return trailOpenMap.get(id);
    }

}