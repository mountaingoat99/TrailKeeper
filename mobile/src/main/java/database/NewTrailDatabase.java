package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import models.ModelTrails;

public class NewTrailDatabase extends DatabaseHelper{

    private final String TAG = "NewTrailDatabase";

    public NewTrailDatabase(Context context) { super(context);}

    //-------------create new row------------------//
    public void AddNewTrail(ModelTrails trail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(getNAME(), trail.getTrailName());
        values.put(getCITY(), trail.getTrailCity());
        values.put(getSTATE(), trail.getTrailState());
        values.put(getCOUNTRY(), trail.getTrailCountry());
        values.put(getLENGTH(), trail.getLength());
        values.put(getLATITUDE(), trail.getLocation().latitude);
        values.put(getLONGITUDE(), trail.getLocation().longitude);
        values.put(getSTATUS(), trail.getTrailStatus());
        values.put(getEASY(), trail.getIsEasy());
        values.put(getMEDIUM(), trail.getIsMedium());
        values.put(getHARD(), trail.getIsHard());

        Log.i(TAG, "Inserting trail " + trail.getTrailName() + " into the DB");

        db.insert(getTableOfflineTrail(), null, values);
    }

    //------------get values from one row----------//
    public ModelTrails GetOffLineTrail() {
        ModelTrails trails = new ModelTrails();
        int row = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " SELECT * FROM " + getTableOfflineTrail() + " LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                row = c.getInt(0);
                trails.setTrailName(c.getString(1));
                trails.setTrailCity(c.getString(2));
                trails.setTrailState(c.getString(3));
                trails.setTrailCountry(c.getString(4));
                trails.setLength(Double.valueOf(c.getString(5)));
                LatLng location = new LatLng(Double.valueOf(c.getString(6)), Double.valueOf(c.getString(7)));
                trails.setLocation(location);
                trails.setTrailStatus(Integer.valueOf(c.getString(8)));
                trails.setIsEasy(Boolean.valueOf(c.getString(9)));
                trails.setIsMedium(Boolean.valueOf(c.getString(10)));
                trails.setIsHard(Boolean.valueOf(c.getString(11)));
                Log.i(TAG, "Fetching trail " + c.getString(1) + " from the DB");
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        // delete the row once we get everything
        if (row > 0) {
            DeleteNewTrail(row);
        }
        return trails;
    }


    //-------------delete one row by ID------------//
    public void DeleteNewTrail(int id) {
        String selectQuery = "DELETE FROM " + getTableOfflineTrail()
                + " WHERE " + getKeyId()+ "= " + id;
        Log.i(TAG, "Deleting trail from the DB");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    //-------------gets the row count--------------//
    public int GetDBRowCount() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " SELECT * FROM " + getTableOfflineTrail();
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
