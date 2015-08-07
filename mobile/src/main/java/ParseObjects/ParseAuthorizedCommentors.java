package ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("AuthorizedCommentors")
public class ParseAuthorizedCommentors extends ParseObject {

    public String getUserObjectID() {
        return getString("userObjectId");
    }

    public void SetUserObjectID(String userId) {
        put("userObjectId", userId);
    }

    public String getUserName() {
        return getString("userName");
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }

    public Boolean getCanComment() { return getBoolean("canComment"); }

    public void setCanComment(Boolean canComment) { put("canComment", canComment); }

    public static ParseQuery<ParseAuthorizedCommentors> getQuery() {
        return ParseQuery.getQuery(ParseAuthorizedCommentors.class);
    }
}
