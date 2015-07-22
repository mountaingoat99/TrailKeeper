package Helpers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.singlecog.trailkeeper.Activites.CreateAccount;
import com.singlecog.trailkeeper.Activites.Settings;
import com.singlecog.trailkeeper.SignIn;

import java.util.Set;
import java.util.UnknownFormatConversionException;

public class CreateAccountHelper {

    private static final String LOG = "CreateAccountHelper";
    private CreateAccount createAccountActivity;
    private Settings settingsActivity;
    private SignIn signInActivity;
    private Context context;

    public CreateAccountHelper(Context context, CreateAccount activity) {
        this.createAccountActivity = activity;
        this.context = context;
    }

    public CreateAccountHelper(Context context, Settings activity) {
        this.settingsActivity = activity;
        this.context = context;
    }

    public CreateAccountHelper(Context context, SignIn activity) {
        this.context = context;
        this.signInActivity = activity;
    }

    // TODO check if the email is verified
    public static boolean isEmailVerified(){
        return true;
    }

    public static Boolean IsAnonUser() {
        return (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser()));
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidUserName(String target) {
        return target.length() >= 6;
    }

    public static boolean isValidPassword(String target){
        return target.length() >= 8;
    }

    // TODO set the failed message
//    private static String CreateAccountErrorMessage(String message){
//        return "Failed to Create Account";
//    }

    public void CreateParseUserAccount(String username, String password, String email){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    createAccountActivity.UpdateCreateAccountSuccessMessage(true, null);
                    Log.i(LOG, "Create Account success");
                } else {
                    createAccountActivity.UpdateCreateAccountSuccessMessage(false, e.getMessage());
                    Log.i(LOG, "Failed Create Account message: " + e.getMessage());
                }
            }
        });
    }

    public void SignIn(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    signInActivity.SignInSuccess(true, null);
                    Log.i(LOG, "Sign In Success");
                }else{
                    signInActivity.SignInSuccess(false, e.getMessage());
                    Log.i(LOG, "Login Failed");
                }
            }
        });

    }

    public void CreateAnonUser() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    settingsActivity.SignedOut(false, e.getMessage());
                    Log.i(LOG, "Create Anon Account Failed");
                } else {
                    settingsActivity.SignedOut(true, null);
                    Log.i(LOG, "Create Anon Account Success");
                }
            }
        });
    }
}
