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
import ParseObjects.ParseTrails;

public class AsyncLoadAllTrailsFromParse extends AsyncTask<Void, Void, Void>{

    @Override
    protected Void doInBackground(Void... params) {
        // Load the ParseTrails
        ParseQuery<ParseTrails> query = ParseTrails.getQuery();
        query.findInBackground(new FindCallback<ParseTrails>() {
            @Override
            public void done(List<ParseTrails> trails, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(trails);
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i("AsyncLoadTrailFromParse",
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
