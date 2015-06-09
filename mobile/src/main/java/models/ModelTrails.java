package models;

public class ModelTrails {

    private static int nextId = 0;

    public ModelTrails()
    {

    }

    public String TrailName;
    public int TrailStatus;
    public String TrailState;

    //TODO I think we'll use what the db sends us after we get that hooked up
    public int TrailId = ++nextId;
}