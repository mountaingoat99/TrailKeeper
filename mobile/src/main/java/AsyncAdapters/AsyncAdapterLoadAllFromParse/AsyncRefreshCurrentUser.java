package AsyncAdapters.AsyncAdapterLoadAllFromParse;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;

public class AsyncRefreshCurrentUser extends AsyncTask<Void, Void, Void> {

    private final String TAG = "AsyncRefreshCurrentUser";

    @Override
    protected Void doInBackground(Void... params) {
        Log.i(TAG, "Refreshing user from Async Task");

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    TrailKeeperApplication.setIsEmailVerified(parseObject.getBoolean("emailVerified"));
                    Log.i(TAG, "Got user and Verified status is: " + parseObject.getBoolean("emailVerified"));
                } else {
                    Log.i(TAG, "Failed to get the user");
                }
            }
        });

        return null;
    }
}
