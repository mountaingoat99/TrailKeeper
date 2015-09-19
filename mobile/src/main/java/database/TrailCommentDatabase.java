package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import models.ModelTrailComments;

public class TrailCommentDatabase extends DatabaseHelper {

    private final String TAG = "TrailCommentDatabase";

    public TrailCommentDatabase(Context context) {
        super(context);
    }

    //-------------create new row------------------//
    public void AddNewComment(ModelTrailComments trailComments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(getObjectId(), trailComments.getObjectID());
        values.put(getTrailName(), trailComments.getTrailName());
        values.put(getCOMMENT(), trailComments.getTrailComments());

        Log.i(TAG, "Inserting new comment for trail: " + trailComments.getTrailName());
        db.insert(getTableOfflineComment(), null, values);
    }

    //------------get values from one row----------//
    public ModelTrailComments GetOffLineTrailComment() {
        ModelTrailComments trailComments = new ModelTrailComments();
        int row = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + getTableOfflineComment() + " LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                row = c.getInt(0);
                trailComments.setObjectID(c.getString(1));
                trailComments.setTrailName(c.getString(2));
                trailComments.setTrailComments(c.getString(3));
                Log.i(TAG, "Fetching trailComment for trail " + c.getString(2) + " from the DB");
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        // delete the row once we get everything
        if (row > 0) {
            DeleteNewTrailComment(row);
        }
        return trailComments;
    }

    //-------------delete one row by ID------------//
    public void DeleteNewTrailComment(int id) {
        String selectQuery = "DELETE FROM " + getTableOfflineComment()
                + " WHERE " + getKeyId()+ "= " + id;
        Log.i(TAG, "Deleting trailComment from the DB");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    //-------------gets the row count--------------//
    public int GetDBRowCount() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " SELECT * FROM " + getTableOfflineComment();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.getCount() > 0) {
            count = c.getCount();
            c.close();
            db.close();
        }
        c.close();
        db.close();
        return count;
    }

}
