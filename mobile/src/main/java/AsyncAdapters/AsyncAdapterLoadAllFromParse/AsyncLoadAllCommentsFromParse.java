package AsyncAdapters.AsyncAdapterLoadAllFromParse;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import ParseObjects.ParseComments;

public class AsyncLoadAllCommentsFromParse extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        //Load the ParseComments
        ParseQuery<ParseComments> query = ParseComments.getQuery();
        query.findInBackground(new FindCallback<ParseComments>() {
            @Override
            public void done(List<ParseComments> comments, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(comments);
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i("LoadCommentsFromParse",
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
