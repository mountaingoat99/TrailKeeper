package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("Comments")
public class ParseComments extends ParseObject {

    public String getTrailObjectID() {
        return getString("trailObjectId");
    }

    public void SetTrailObjectID(String trailID) {
        put("trailObjectId", trailID);
    }

    public String getTrailName() {
        return getString("trailName");
    }

    public void setTrailName(String trailName) {
        put("trailName", trailName);
    }

    public String getUserObjectID() {
        return getString("userObjectId");
    }

    public void SetUserObjectID(String trailID) {
        put("userObjectId", trailID);
    }

    public String getUserName() {
        return getString("userName");
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }

    public String getComment() {
        return getString("comment");
    }

    public void SetComment(String comment) {
        put("comment", comment);
    }

    public Date getCreatedAt(){
        return getDate("createdAt");
    }

    public static ParseQuery<ParseComments> getQuery() {
        return ParseQuery.getQuery(ParseComments.class);
    }
}
