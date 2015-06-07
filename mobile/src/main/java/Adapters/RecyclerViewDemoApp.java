package Adapters;

import android.app.Application;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.DemoModel;

/**
 * Created by Jeremey on 6/2/2015.
 */
public class RecyclerViewDemoApp extends Application {

    private static List<DemoModel> demoData;
    private static SparseArray<DemoModel> demoMap;

    @Override
    public void onCreate() {
        super.onCreate();
        Random r = new Random();
        demoData = new ArrayList<>();
        demoMap = new SparseArray<>();
        for (int i = 0; i < 20; i++) {
            DemoModel model = new DemoModel();
            model.label = "Test Label No. " + i;
            demoData.add(model);
            demoMap.put(model.id, model);
        }
    }

    public static final List<DemoModel> getDemoData() {
        return new ArrayList<>(demoData);
    }

    public static final List<DemoModel> addItemToList(DemoModel model, int position) {
        demoData.add(position, model);
        demoMap.put(model.id, model);
        return new ArrayList<>(demoData);
    }

    public static final List<DemoModel> removeItemFromList(int position) {
        demoData.remove(position);
        demoMap.remove(demoData.get(position).id);
        return new ArrayList<>(demoData);
    }

    public static DemoModel findById(int id) {
        return demoMap.get(id);
    }

}