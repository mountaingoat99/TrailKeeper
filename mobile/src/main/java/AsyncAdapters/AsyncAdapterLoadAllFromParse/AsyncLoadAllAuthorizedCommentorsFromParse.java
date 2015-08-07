package AsyncAdapters.AsyncAdapterLoadAllFromParse;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import ParseObjects.ParseAuthorizedCommentors;

public class AsyncLoadAllAuthorizedCommentorsFromParse extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        ParseQuery<ParseAuthorizedCommentors> query = ParseAuthorizedCommentors.getQuery();
        query.findInBackground(new FindCallback<ParseAuthorizedCommentors>() {
            @Override
            public void done(List<ParseAuthorizedCommentors> list, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(list);
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i("LoadAuthComFromParse",
                                        "Could not get all: "
                                                + e.getMessage());
                            }
                        }
                    };
                } else {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }
}
