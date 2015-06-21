package models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

public class ModelTrails {

    private static int nextId = 0;

    public ModelTrails()
    {

    }

    public int getTrailID() {
        return TrailID;
    }

    public void setTrailID(int trailID) {
        TrailID = trailID;
    }

    public int TrailID;
    public String TrailName;
    public int TrailStatus;
    public String TrailCity;
    public String TrailState;
    public ParseGeoPoint GeoLocation;
    public float distance;

    public float getDistance() {
        return distance;
    }
//TODO I think we'll use what the db sends us after we get that hooked up
    //public int TrailId = ++nextId;
}