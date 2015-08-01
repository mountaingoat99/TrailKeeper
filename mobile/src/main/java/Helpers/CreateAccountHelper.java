package Helpers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.singlecog.trailkeeper.Activites.AccountSettings;
import com.singlecog.trailkeeper.Activites.CreateAccount;
import com.singlecog.trailkeeper.Activites.Settings;
import com.singlecog.trailkeeper.Activites.SignIn;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.Activites.UpdateAccount;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountHelper {

    private static final String LOG = "CreateAccountHelper";
    private CreateAccount createAccountActivity;
    private AccountSettings settingsActivity;
    private SignIn signInActivity;
    private final String SIGNINACTIVITY = "SignIn";
    private final String UPDATEACCOUNTACTIVITY = "UpdateAccount";
    private String whichActivity;
    private UpdateAccount updateAccountActivity;
    private static Boolean emailVerified = false;
    private Context context;

    public CreateAccountHelper(Context context, CreateAccount activity) {
        this.createAccountActivity = activity;
        this.context = context;
    }

    public CreateAccountHelper(Context context, AccountSettings activity) {
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

    public void UpdateUserName(final String newUserName) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(newUserName);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Update Username Success");
                    updateAccountActivity.UpdateUserNameSuccess(true, null, newUserName);
                } else {
                    Log.i(LOG, "Update Username Fail");
                    updateAccountActivity.UpdateUserNameSuccess(false, e.getMessage(), null);
                }
            }
        });
    }

    public void UpdateEmail(final String newEmail) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setEmail(newEmail);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Update Emai; Success");
                    updateAccountActivity.UpdateEmailSuccess(true, null, newEmail);
                } else {
                    Log.i(LOG, "Update Email Fail");
                    updateAccountActivity.UpdateEmailSuccess(false, e.getMessage(), null);
                }
            }
        });
    }

    // this is ridiculous, but in order to get the verify email to resend is
    // to update the email with a fake one, then update it again with the correct one.
    public void ResendVerifyUserEmail() {
        ParseUser user = ParseUser.getCurrentUser();
        final String realEmail = user.getEmail();
        user.setEmail("trailKeeper@email.com");
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "First Resend Email success");
                    ResendRealEmailAfterCreatingFake(realEmail, null, true);
                } else {
                    Log.i(LOG, "First Resend Email Failed message: " + e.getMessage());
                    ResendRealEmailAfterCreatingFake(realEmail, e.getMessage(), false);
                }
            }
        });
    }

    private void ResendRealEmailAfterCreatingFake(String realEmail, String failMessage, boolean valid) {
        if (valid) {
            ParseUser user = ParseUser.getCurrentUser();
            user.setEmail(realEmail);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i(LOG, "Second Resend Email success");
                        updateAccountActivity.VerifySuccess(true, null);
                    } else {
                        Log.i(LOG, "First Resend Email Failed message: " + e.getMessage());
                        updateAccountActivity.VerifySuccess(false, e.getMessage());
                    }
                }
            });
        } else {
            updateAccountActivity.VerifySuccess(valid, failMessage);
        }
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

    public static void CheckUserVerified(){
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Got the User");
                    TrailKeeperApplication.setIsEmailVerified(parseUser.getBoolean("emailVerified"));
                } else {
                    Log.i(LOG, "Failed to get user");
                    TrailKeeperApplication.setIsEmailVerified(false);
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
                    TrailKeeperApplication.setIsEmailVerified(parseUser.getBoolean("emailVerified"));
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
                        updateAccountActivity.SignInSuccess(false, e.getMessage(), null);
                    }
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
                    if (whichActivity.equals(SIGNINACTIVITY)) {
                        signInActivity.PasswordResetSuccess(true, null);
                    } else {
                        updateAccountActivity.PasswordResetSuccess(true, null);
                    }
                } else {
                    Log.i(LOG, "Password Reset Fail");
                    if (whichActivity.equals(SIGNINACTIVITY)) {
                        signInActivity.PasswordResetSuccess(false, e.getMessage());
                    } else {
                        updateAccountActivity.PasswordResetSuccess(false, e.getMessage());
                    }
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
