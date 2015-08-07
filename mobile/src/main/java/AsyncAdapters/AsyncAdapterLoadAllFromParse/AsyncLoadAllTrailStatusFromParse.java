package AsyncAdapters.AsyncAdapterLoadAllFromParse;


import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import ParseObjects.ParseTrailStatus;

public class AsyncLoadAllTrailStatusFromParse extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        ParseQuery<ParseTrailStatus> query = ParseTrailStatus.getQuery();
        query.findInBackground(new FindCallback<ParseTrailStatus>() {
            @Override
            public void done(List<ParseTrailStatus> list, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(list);
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i("LoadTrailStatFromParse",
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
