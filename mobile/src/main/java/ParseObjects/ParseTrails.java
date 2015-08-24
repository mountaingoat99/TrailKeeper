package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.Date;

@ParseClassName("Trails")
public class ParseTrails extends ParseObject {

    public String getTrailName() {
        return getString("trailName");
    }

    public void setTrailName(String trailName) {
        put("trailName", trailName);
    }

    public String getCity(){
        return getString("city");
    }

    public void setCity(String city){
        put("city", city);
    }

    public String getState(){
        return getString("state");
    }

    public void setState(String state){
        put("state", state);
    }

    public Integer getStatus() {
        return getInt("status");
    }

    public void setStatus(Integer status) {
        put("status", status);
    }

    public Date getUpdatedAt(){
        return getDate("updatedAt");
    }

    public String getCreatedBy() { return getString("createdBy"); }

    public void setCreatedBy(String userName) { put("createdBy", userName); }

    public Boolean getPrivate() { return getBoolean("private"); }

    public void setPrivate(boolean isPrivate) { put("private", isPrivate); }

    public JSONArray getSkillLevels() { return getJSONArray("skillLevels"); }

    public void setSkillLevels(JSONArray skillLevels) { put("skillLevels", skillLevels); }

    public static ParseQuery<ParseTrails> getQuery() {
        return ParseQuery.getQuery(ParseTrails.class);
    }
}
