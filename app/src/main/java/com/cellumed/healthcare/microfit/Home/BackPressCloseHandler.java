package com.cellumed.healthcare.microfit.Home;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.cellumed.healthcare.microfit.R;

/**
 *
 */

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }


    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                activity.finishAndRemoveTask();
            } else
            {
                ActivityCompat.finishAffinity(activity);
                // activity.finish();
            }
            toast.cancel();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public void showGuide() {
        String r=activity.getResources().getString(R.string.backCheck);
        toast = Toast.makeText(activity,
                r, Toast.LENGTH_SHORT);
        toast.show();
    }
}