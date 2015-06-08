package AsyncAdapters;

import android.app.Application;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import models.ModelTrailComments;

public class RecyclerViewAsyncOneTrailComments extends Application {

    private static List<ModelTrailComments> commentData;
    private static SparseArray<ModelTrailComments> commentMap;

    @Override
    public void onCreate() {
        super.onCreate();
        commentData = new ArrayList<>();
        commentMap = new SparseArray<>();
        for (int i = 1; i < 21; i++) {
            ModelTrailComments model = new ModelTrailComments();
            model.TrailComments = "This Trail is rad";
            model.CommentDate = "May 4th";
            commentData.add(model);
            commentMap.put(model.CommentId, model);
        }
    }

    public static List<ModelTrailComments> getTrailCommentData() {
        return new ArrayList<>(commentData);
    }

    public static List<ModelTrailComments> addItemToList(ModelTrailComments model, int position) {
        commentData.add(position, model);
        commentMap.put(model.CommentId, model);
        return new ArrayList<>(commentData);
    }

    public static List<ModelTrailComments> removeItemFromList(int position) {
        commentData.remove(position);
        commentMap.remove(commentData.get(position).CommentId);
        return new ArrayList<>(commentData);
    }

    public static ModelTrailComments findById(int id) {
        return commentMap.get(id);
    }
}
