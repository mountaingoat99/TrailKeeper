package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "OFFLINE_TRAILS";
    private static final String TABLE_OFFLINE_TRAIL = "offline_trail";
    private static final String KEY_ID = "id";
    private static final String NAME = "name";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String COUNTRY = "country";
    private static final String LENGTH = "length";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String STATUS = "status";
    private static final String EASY = "easy";
    private static final String MEDIUM = "medium";
    private static final String HARD = "hard";

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public static String getTableOfflineTrail() {
        return TABLE_OFFLINE_TRAIL;
    }

    public static String getKeyId() {
        return KEY_ID;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getCITY() {
        return CITY;
    }

    public static String getSTATE() {
        return STATE;
    }

    public static String getCOUNTRY() {
        return COUNTRY;
    }

    public static String getLENGTH() {
        return LENGTH;
    }

    public static String getLATITUDE() {
        return LATITUDE;
    }

    public static String getLONGITUDE() {
        return LONGITUDE;
    }

    public static String getSTATUS() {
        return STATUS;
    }

    public static String getEASY() {
        return EASY;
    }

    public static String getMEDIUM() {
        return MEDIUM;
    }

    public static String getHARD() {
        return HARD;
    }

    public static final String CREATE_OFFLINE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_OFFLINE_TRAIL + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, " + CITY + " TEXT, " + STATE + " TEXT, "
            + COUNTRY + " TEXT, " + LENGTH + " TEXT, " + LATITUDE + " TEXT, "
            + LONGITUDE + " TEXT, " + STATUS + " TEXT, " + EASY + " TEXT, "
            + MEDIUM + " TEXT, " + HARD + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_OFFLINE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:

                break;
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown new Version" + newVersion);
        }
    }
}
