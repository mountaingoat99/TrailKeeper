package Helpers;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogHelper {

    public static ProgressDialog ShowProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.show();

        return dialog;
    }
}
