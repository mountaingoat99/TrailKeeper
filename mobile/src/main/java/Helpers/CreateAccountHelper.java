package Helpers;

import android.content.Context;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.singlecog.trailkeeper.Activites.CreateAccount;
import com.singlecog.trailkeeper.Activites.Settings;
import com.singlecog.trailkeeper.Activites.SignIn;
import com.singlecog.trailkeeper.UpdateAccount;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountHelper {

    private static final String LOG = "CreateAccountHelper";
    private CreateAccount createAccountActivity;
    private Settings settingsActivity;
    private SignIn signInActivity;
    private final String SIGNINACTIVITY = "SignIn";
    private final String UPDATEACCOUNTACTIVITY = "UpdateAccount";
    private String whichActivity;
    private UpdateAccount updateAccountActivity;
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
        whichActivity = SIGNINACTIVITY;
    }

    public CreateAccountHelper(Context context, UpdateAccount activity) {
        this.context = context;
        this.updateAccountActivity = activity;
        whichActivity = UPDATEACCOUNTACTIVITY;
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

    public void UpdateParseUserEmail(String email, final boolean isVerify) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setEmail(email);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.i(LOG, "Create Account success");
                    if (isVerify) {
                        updateAccountActivity.VerifySuccess(true, null);
                    } else {

                    }
                } else {
                    Log.i(LOG, "Failed Create Account message: " + e.getMessage());
                    if (isVerify) {
                        updateAccountActivity.VerifySuccess(false, e.getMessage());
                    } else {

                    }
                }
            }
        });
    }

    public void CreateParseUserAccount(String username, String password, String email){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        // TODO Debug Password
        user.put("DebugPassword", password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Create Account success");
                    createAccountActivity.UpdateCreateAccountSuccessMessage(true, null);
                } else {
                    Log.i(LOG, "Failed Create Account message: " + e.getMessage());
                    createAccountActivity.UpdateCreateAccountSuccessMessage(false, e.getMessage());
                }
            }
        });
    }

    public void SignIn(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Sign In Success");
                    if (whichActivity.equals(SIGNINACTIVITY)) {
                        signInActivity.SignInSuccess(true, null);
                    } else {
                        String email = ParseUser.getCurrentUser().getEmail();
                        updateAccountActivity.SignInSuccess(true, null, email);
                    }
                } else {
                    Log.i(LOG, "Login Failed");
                    if (whichActivity.equals(SIGNINACTIVITY)) {
                        signInActivity.SignInSuccess(false, e.getMessage());
                    } else {
                        updateAccountActivity.SignInSuccess(false, e.getMessage(), null);                    }
                }
            }
        });
    }

    public void DeleteUser(ParseUser parseUser) {
        List<ParseUser> currentUser = new ArrayList<>();
        currentUser.add(parseUser);
        ParseUser.deleteAllInBackground(currentUser, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Delete Account Success");
                    settingsActivity.DeleteSuccessOrFail(true, null);
                } else {
                    Log.i(LOG, "Delete Account Fail");
                    settingsActivity.DeleteSuccessOrFail(false, e.getMessage());
                }
            }
        });
    }

    public void ResetPassword(String email){
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Password Reset Success");
                    signInActivity.PasswordResetSuccess(true, null);
                } else {
                    Log.i(LOG, "Password Reset Fail");
                    signInActivity.PasswordResetSuccess(false, e.getMessage());
                }
            }
        });
    }

    public void FindUserName(String email) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    String userName = null;
                    for (ParseUser parseUser : list) {
                        if (!parseUser.getUsername().isEmpty())
                            userName = parseUser.getUsername();
                    }
                    if(userName != null) {
                        signInActivity.FindUsernameSuccess(true, userName);
                    } else {
                        signInActivity.FindUsernameSuccess(false, null);
                    }
                } else {
                    signInActivity.FindUsernameSuccess(false, null);
                }
            }
        });
    }

    public void CreateAnonUser() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    Log.i(LOG, "Create Anon Account Failed");
                    settingsActivity.SignedOut(false, e.getMessage());
                } else {
                    Log.i(LOG, "Create Anon Account Success");
                    settingsActivity.SignedOut(true, null);
                }
            }
        });
    }
}
