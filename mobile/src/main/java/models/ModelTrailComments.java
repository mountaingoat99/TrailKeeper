package models;

/**
 * Created by Jeremey on 6/7/2015.
 */
public class ModelTrailComments {

    private static int nextId = 0;

    public ModelTrailComments()
    {

    }

    public String TrailName;
    public String TrailComments;

    //TODO I think we'll use what the db sends us after we get that hooked up
    public int CommentId = ++nextId;
}
