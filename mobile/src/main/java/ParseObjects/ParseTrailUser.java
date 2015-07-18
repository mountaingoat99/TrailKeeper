package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseQuery;

import java.util.Date;

@ParseClassName("User")
public class ParseTrailUser extends com.parse.ParseUser {

    public ParseTrailUser(String _username, String _password, String _email) {
        SetUsername(_username);
        SetPassword(_password);
        setEmail(_email);
    }

    public Date getUpdatedAt() { return getDate("updatedAt"); }

    public String getObjectId() { return getObjectId(); }

    public void setObjectId( String objectID) {put("objectID", objectID); }

    public String getUsername() { return getString("username"); }

    public void SetUsername(String username) { put("username", username);    }

    public void SetPassword(String password) { put("password", password); }

    public String getEmail() { return getString("email"); }

    public void setEmail(String email) { put("email", email); }

    public Date getCreatedAt() { return getDate("createdAt"); }

    public static ParseQuery<ParseTrailUser> getUserQuery() {
        return ParseQuery.getQuery(ParseTrailUser.class);
    }

}
