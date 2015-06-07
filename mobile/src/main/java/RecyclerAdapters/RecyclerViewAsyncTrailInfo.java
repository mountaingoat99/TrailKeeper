package RecyclerAdapters;

import android.app.Application;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.ModelOpenClosedTrails;

public class RecyclerViewAsyncTrailInfo extends Application {

    private static List<ModelOpenClosedTrails> demoData;
    private static SparseArray<ModelOpenClosedTrails> demoMap;

    @Override
    public void onCreate() {
        super.onCreate();
        Random r = new Random();
        demoData = new ArrayList<>();
        demoMap = new SparseArray<>();
        for (int i = 1; i < 21; i++) {
            ModelOpenClosedTrails model = new ModelOpenClosedTrails();
            model.TrailName = "Test Label No. " + i;
            demoData.add(model);
            demoMap.put(model.TrailId, model);
        }
    }

    public static List<ModelOpenClosedTrails> getDemoData() {

        demoData = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            ModelOpenClosedTrails model = new ModelOpenClosedTrails();
            model.TrailName = "Test trail " + i;
            model.TrailStatus = 2;
            demoData.add(model);
        }

        return new ArrayList<>(demoData);
    }

    public static List<ModelOpenClosedTrails> addItemToList(ModelOpenClosedTrails model, int position) {
        demoData.add(position, model);
        demoMap.put(model.TrailId, model);
        return new ArrayList<>(demoData);
    }

    public static List<ModelOpenClosedTrails> removeItemFromList(int position) {
        demoData.remove(position);
        demoMap.remove(demoData.get(position).TrailId);
        return new ArrayList<>(demoData);
    }

    public static ModelOpenClosedTrails findById(int id) {
        return demoMap.get(id);
    }

}