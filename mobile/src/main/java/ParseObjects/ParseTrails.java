package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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

    public String getCountry(){
        return getString("country");
    }

    public void setCountry(String country){
        put("country", country);
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

    public boolean getSkillEasy() { return getBoolean("skillEasy"); }

    public void setSkillEasy(boolean isEasy) { put("skillEasy", isEasy);}

    public boolean getSkillMedium() { return getBoolean("skillMedium"); }

    public void setSkillMedium(boolean isMedium) { put("skillMedium", isMedium);}

    public boolean getSkillHard() { return getBoolean("skillHard"); }

    public void setSkillHard(boolean isHard) { put("skillHard", isHard);}

    public double getLength() {return getDouble("distance");}

    public void setLength(double distance) { put("distance", distance);}

    public ParseGeoPoint getLocation() {return getParseGeoPoint("geoLocation");}

    public void setLocation(ParseGeoPoint geoLocation) { put("geoLocation", geoLocation);}

    public String getMapLink() {
        return getString("mapLink");
    }

    public void setMapLink(String mapLink) {
        put("mapLink", mapLink);
    }

    public String getLastUpdateByUserObjectId() { return getString("lastUpdatedByUserObjectId"); }

    public void setlastUpdatedByUserObjectId(String lastUpdatedByUserObjectId) { put("lastUpdatedByUserObjectId", lastUpdatedByUserObjectId);}

    public static ParseQuery<ParseTrails> getQuery() {
        return ParseQuery.getQuery(ParseTrails.class);
    }
}
