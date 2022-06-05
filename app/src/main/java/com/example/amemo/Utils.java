package com.example.amemo;

import android.graphics.Color;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
public class Utils extends AppCompatActivity{
    protected static boolean useThemeStatusBarColor = false;
    protected static boolean useStatusBarColor = true;
    public static void setStatusBar(AppCompatActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (useThemeStatusBarColor) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colortheme));
        } else {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (useStatusBarColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
