package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("comments")
public class ParseComments extends ParseObject {

    public Integer getCommentID() {
        return getInt("commentID");
    }

    public void SetCommentID(Integer commentId) {
        put("commentID", commentId);
    }

    public Integer getTrailID() {
        return getInt("trailID");
    }

    public void SetTrailID(Integer trailID) {
        put("trailID", trailID);
    }

    public String getTrailName() {
        return getString("TrailName");
    }

    public void SetTrailName(String trailName) {
        put("TrailName", trailName);
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

    public void setCreatedAt(Date createdAt) {
        put("createdAt", createdAt);
    }
}
