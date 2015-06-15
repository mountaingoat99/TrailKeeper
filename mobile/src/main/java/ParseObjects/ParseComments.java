package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

@ParseClassName("comments")
public class ParseComments extends ParseObject {

    public Integer getTrailID() {
        return getInt("TrailID");
    }

    public void SetTrailID(Integer trailID) {
        put("TrailID", trailID);
    }

    public String getComment() {
        return getString("Comment");
    }

    public void SetComment(String comment) {
        put("Cmment", comment);
    }

    public Date getCreatedAt(){
        return getDate("createdAt");
    }

    public void setCreatedAt(Date createdAt) {
        put("createdAt", createdAt);
    }

    public static ParseQuery<ParseComments> getQuery() {
        return ParseQuery.getQuery(ParseComments.class);
    }
}
