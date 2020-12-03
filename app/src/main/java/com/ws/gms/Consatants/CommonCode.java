package com.ws.gms.Consatants;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;


import com.ws.gms.ChangeLanguageActivity;
import com.ws.gms.R;

import java.util.Locale;

public class CommonCode {

    public Boolean checkConnection(Context context) {
        if (isOnline(context)) {
            return true;
        } else {
            return false;
        }
    }

    public void setupUI(View view, final Context context) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hidekeyboard(context);
                    return false;
                }
            });
        }
    }

    void hidekeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((FragmentActivity) context).getCurrentFocus();
        if (v != null) {
            ((FragmentActivity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void AlertDialog_Pbtn(final Context context, String Title, String Message, String ptext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(Title);
            builder.setIcon(R.drawable.warning_sign);
            builder.setMessage(Message);
            builder.setPositiveButton(ptext, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  InputMethodManager inputMethodManager = (InputMethodManager) ((FragmentActivity)context).getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //  inputMethodManager.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_HIDDEN, 0);
                    dialog.cancel();

                }
            });

            builder.create();
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isValidString(Context ctx, String str) {
        if (str != null && str.length() >= 1 && !str.equals("") && !str.equals("null") && !str.trim().equals("")) {
            return true;
        }
        return false;
    }

    public boolean isValidInt(Context ctx, int str) {
        if (str >= 0) {
            return true;
        }
        return false;
    }


    // Simple Email Id Validation
    public boolean isValidMEmail(Context ctx, String str) {
        if (str.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
            return true;
        }
        return false;
    }

    // Simple MobileNo Validation
    public boolean isValidMobileNo(Context ctx, String str) {
        if (str != null && str.length() == 10 && !str.equals("") && !str.equals("null")) {
            return true;
        }
        return false;
    }

    // Simple Pincode Validation
    public boolean isValidPincode(Context ctx, String str) {
        if (str != null && str.length() == 6 && !str.equals("") && !str.equals("null")) {
            return true;
        }
        return false;
    }


    public void updateLocaleIfNeeded(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        if (sharedPreferences.contains(ChangeLanguageActivity.LANGUAGE_SETTING)) {
            String locale = sharedPreferences.getString(
                    ChangeLanguageActivity.LANGUAGE_SETTING, "");
            Locale localeSetting = new Locale(locale);

            if (!localeSetting.equals(Locale.getDefault())) {
                Resources resources = ctx.getResources();
                Configuration conf = resources.getConfiguration();
                conf.locale = localeSetting;
                resources.updateConfiguration(conf,
                        resources.getDisplayMetrics());

//                Intent refresh = new Intent(this, LoginActivity.class);
//                startActivity(refresh);
//                finish();
            }
        }
    }
}

