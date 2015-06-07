package models;

/**
 * Created by Jeremey on 6/2/2015.
 */

public class ModelOpenClosedTrails {

    private static int nextId = 0;

    public ModelOpenClosedTrails()
    {

    }

    public String TrailName;
    public int TrailStatus;
    public int TrailId = ++nextId;
}