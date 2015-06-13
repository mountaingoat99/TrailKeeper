package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("trails")
public class ParseTrails extends ParseObject {

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
}
