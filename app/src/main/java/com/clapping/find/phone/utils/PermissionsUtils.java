package com.clapping.find.phone.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.clapping.find.phone.R;
import com.clapping.find.phone.log.Log;

public class PermissionsUtils {
    public static final int REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATION = 1005;
    public static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1006;

    /**
     * 打开电池优化界面
     *
     * @param activity
     */
    public static void showBatteryIgnoreTip(Activity activity) {
        if (!isBatteryOptimizationIgnored(activity) && hasBatteryOptimizationIgnoreActivity(activity)) {
            final Dialog dialog = new Dialog(activity);
            dialog.setCancelable(false);
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_request_permission, null);
            dialog.setContentView(dialogView);
            TextView permissionTitle = dialogView.findViewById(R.id.permission_title);
            TextView permissionContent = dialogView.findViewById(R.id.permission_content);
            int titleResId = R.string.ignore_battery_optimization_title;
            int descResId = R.string.ignore_battery_optimization_desc;
            int actionResId = R.string.str_allow;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                titleResId = R.string.background_running_title;
                descResId = R.string.background_running_desc;
            }
            permissionTitle.setText(titleResId);
            String permissionText = activity.getResources().getString(descResId);
            permissionContent.setText(Html.fromHtml(permissionText));
            TextView onButton = dialogView.findViewById(R.id.btn_confirm);
            onButton.setText(actionResId);
            onButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ignoreBatteryOptimization(activity);
                }
            });
            TextView cancelButton = dialogView.findViewById(R.id.btn_cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            try {
                WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                p.width = (int) (dm.widthPixels * 0.9f);
                p.dimAmount = 0.8f;
                p.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(p);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 忽略电池优化
     */
    private static void ignoreBatteryOptimization(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager powerManager = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
                boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
                Log.iv(Log.TAG, "hasIgnored : " + hasIgnored);
                if (!hasIgnored) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivityForResult(intent, REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATION);
                    }
                }
            }
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }

    public static boolean isBatteryOptimizationIgnored(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager powerManager = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
                return powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
            }
        } catch (Exception e) {
        }
        return true;
    }

    private static boolean hasBatteryOptimizationIgnoreActivity(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
        }
        return false;
    }
}