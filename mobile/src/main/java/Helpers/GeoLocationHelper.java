package Helpers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.ModelTrails;

public class GeoLocationHelper {

    public static float GetClosestTrails(ModelTrails trail, LatLng currentLocation){
        double latTrail = trail.GeoLocation.getLatitude();
        double longTrail = trail.GeoLocation.getLongitude();
        double latLocation = currentLocation.latitude;            // TODO crashes here without Google services or location turned on
        double longLocation = currentLocation.longitude;

        float[] dist = new float[1];
        Location.distanceBetween(latLocation, longLocation, latTrail, longTrail, dist);
        return dist[0] * 0.000621371192f;
    }

    public static void SortTrails(List<ModelTrails> trails) {
        Collections.sort(trails, new Comparator<ModelTrails>() {
            @Override
            public int compare(ModelTrails lhs, ModelTrails rhs) {
                Float dis1 = lhs.getDistance();
                Float dis2 = rhs.getDistance();

                if (dis1.compareTo(dis2) < 0)
                    return -1;
                else if (dis1.compareTo(dis2) > 0)
                    return 1;
                else
                    return 0;
            }
        });
    }
}
