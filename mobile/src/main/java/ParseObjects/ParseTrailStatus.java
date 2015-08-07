package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

@ParseClassName("TrailStatus")
public class ParseTrailStatus extends ParseObject {

    public Integer getupdateStatusPin() { return  getInt("updateStatusPin"); }

    public void setUpdateStatusPin(Integer pin) { put("updateStatusPin", pin); }

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

    public JSONArray getAuthorizedUsers() {return getJSONArray("authorizedUsers"); }

    public void setAuthorizedUsers(String authorizedUsers) { put("authorizedUsers", authorizedUsers); }

    public JSONArray getAuthorizedUserNames() {return getJSONArray("authorizedUserNames"); }

    public void setAuthorizedUserNames(String authorizedUserNames) { put("authorizedUserNames", authorizedUserNames); }

    public static ParseQuery<ParseTrailStatus> getQuery() {
        return ParseQuery.getQuery(ParseTrailStatus.class);
    }
}
