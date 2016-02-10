package org.devel.bookowl.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.devel.bookowl.R;
import org.devel.bookowl.activity.BaseActivity;

public class AutoScanDialog extends DialogFragment {

    private static String TAG = AutoScanDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
            .setTitle(getResources().getString(R.string.dialog_autoscan_title))
            .setMessage(getResources().getString(R.string.dialog_autoscan_message))
            .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((BaseActivity)getActivity()).doPositiveClickInAlertDialog();
                            dialog.dismiss();
                        }
                    }
            )
            .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }
            );

        return b.create();
    }

}
