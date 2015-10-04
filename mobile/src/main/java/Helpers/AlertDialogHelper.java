package Helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
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

    public static void showCustomAlertDialogForPlayServices(final Context context, String title, String message) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_dialog_play_services);
        TextView titletext = (TextView)alertDialog.findViewById(R.id.txt_title);
        TextView messagetext = (TextView)alertDialog.findViewById(R.id.txt_message);
        Button btnCancel = (Button)alertDialog.findViewById(R.id.btn_cancel);
        Button btnOkay = (Button)alertDialog.findViewById(R.id.btn_okay);

        titletext.setText(title);
        messagetext.setText(message);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                final String appPackageName = GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE; // getPackageName() from Context or Activity object
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        alertDialog.show();
    }

    public static void showCustomAlertDialogForNoGPS(final Context context, String title, String message) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_dialog_gps);
        TextView titletext = (TextView)alertDialog.findViewById(R.id.txt_title);
        TextView messagetext = (TextView)alertDialog.findViewById(R.id.txt_message);
        Button btnCancel = (Button)alertDialog.findViewById(R.id.btn_cancel);
        Button btnOkay = (Button)alertDialog.findViewById(R.id.btn_okay);
        alertDialog.setCanceledOnTouchOutside(true);

        titletext.setText(title);
        messagetext.setText(message);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(gpsOptionsIntent);

            }
        });
        alertDialog.show();
    }
}
