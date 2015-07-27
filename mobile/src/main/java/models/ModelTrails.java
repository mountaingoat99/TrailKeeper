package models;

import com.parse.ParseGeoPoint;

public class ModelTrails {

    private static int nextId = 0;
    private static CharSequence[] trailStatusNames;

    public ModelTrails()
    {

    }

    public int getTrailID() {
        return TrailID;
    }

    public void setTrailID(int trailID) {
        TrailID = trailID;
    }

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public String ObjectID;
    public int TrailID;
    public String TrailName;
    public int TrailStatus;
    public String TrailCity;
    public String TrailState;
    public ParseGeoPoint GeoLocation;
    public float distance;

    public static CharSequence[] getTrailStatusNames() {
        return trailStatusNames = new CharSequence[]{"Open", "Closed", "Unknown"};
    }

    public float getDistance() {
        return distance;
    }
//TODO I think we'll use what the db sends us after we get that hooked up
    //public int TrailId = ++nextId;
}