package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.Date;

@ParseClassName("trails")
public class ParseTrails extends ParseObject {

    public String GetObjectID() { return getObjectId(); }

    public void SetObjectID( String objectID) {put("ObjectID", objectID); }

    public Integer getTrailID() {
        return getInt("TrailID");
    }

    public void SetTrailID(Integer trailID) {
        put("TrailID", trailID);
    }

    public String getTrailName() {
        return getString("TrailName");
    }

    public void SetTrailName(String trailName) {
        put("TrailName", trailName);
    }

    public String getCity(){
        return getString("City");
    }

    public void SetCity(String city){
        put("City", city);
    }

    public String getState(){
        return getString("State");
    }

    public void SetState(String state){
        put("State", state);
    }

    public Integer getStatus() {
        return getInt("Status");
    }

    public void SetStatus(Integer status) {
        put("Status", status);
    }

    public JSONArray getComments(){
        return getJSONArray("Comments");
    }

    public void setComment(String comment){
        put("Comment", comment);
    }

    public JSONArray getCommentDate(){
        return getJSONArray("CommentDate");
    }

    public void setCommentDate(String commentDate){
        put("CommentDate", commentDate);
    }

    public Date getUpdatedAt(){
        return getDate("updateAt");
    }

    public void setUpdatedAt(Date updateAt) {
        put("updateAt", updateAt);
    }

    public static ParseQuery<ParseTrails> getQuery() {
        return ParseQuery.getQuery(ParseTrails.class);
    }
}
