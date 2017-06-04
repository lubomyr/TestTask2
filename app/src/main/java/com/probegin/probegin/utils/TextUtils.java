package com.probegin.probegin.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;


public class TextUtils {
    public static void showMessage(Context context, String str) {
        if ((str != null) && !str.isEmpty()) {
            final AlertDialog alert = new AlertDialog.Builder(context)
                    .setMessage(str).setPositiveButton("OK", null).show();
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }
}
