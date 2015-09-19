package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import models.ModelTrailStatus;

public class TrailStatusDatabase extends DatabaseHelper {

    private final String TAG = "TrailStatusDatabase";

    public TrailStatusDatabase(Context context) {
        super(context);
    }

    //-------------create new row------------------//
    public void AddNewStatus(ModelTrailStatus trailStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(getObjectId(), trailStatus.getObjectID());
        values.put(getTrailName(), trailStatus.getTrailName());
        values.put(getCHOICE(), trailStatus.getChoice());

        Log.i(TAG, "Inserting new status for trail: " + trailStatus.getObjectID() + " choice: " + trailStatus.getChoice());
        db.insert(getTableOfflineStatus(), null, values);
    }

    //------------get values from one row----------//
    public ModelTrailStatus GetOffLineTrailStatus() {
        ModelTrailStatus trailStatus = new ModelTrailStatus();
        int row = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + getTableOfflineStatus() + " LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                row = c.getInt(0);
                trailStatus.setObjectID(c.getString(1));
                trailStatus.setTrailName(c.getString(2));
                trailStatus.setChoice(Integer.valueOf(c.getString(3)));
                Log.i(TAG, "Fetching trailStatus " + c.getString(1) + " from the DB");
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        // delete the row once we get everything
        if (row > 0) {
            DeleteNewTrailStatus(row);
        }
        return trailStatus;
    }

    //-------------delete one row by ID------------//
    public void DeleteNewTrailStatus(int id) {
        String selectQuery = "DELETE FROM " + getTableOfflineStatus()
                + " WHERE " + getKeyId()+ "= " + id;
        Log.i(TAG, "Deleting trailStatus from the DB");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    //-------------gets the row count--------------//
    public int GetDBRowCount() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " SELECT * FROM " + getTableOfflineStatus();
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
