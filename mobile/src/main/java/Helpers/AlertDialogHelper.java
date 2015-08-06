package Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

public class AlertDialogHelper {

//    public static void showAlertDialog(Context context,String title, String message) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//        alertDialog.setTitle(title);
//        alertDialog.setMessage(message);
//
//        alertDialog.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        alertDialog.create();
//        alertDialog.show();
//    }

    public static void showCustomAlertDialog(Context context, String title, String message) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_dialog);
        TextView titletext = (TextView)alertDialog.findViewById(R.id.txt_title);
        TextView messagetext = (TextView)alertDialog.findViewById(R.id.txt_message);
        Button btnOkay = (Button)alertDialog.findViewById(R.id.btn_okay);

        titletext.setText(title);
        messagetext.setText(message);

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
